@file:JvmSynthetic

package com.octacore.rexpay.utils

import org.bouncycastle.bcpg.ArmoredInputStream
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPCompressedData
import org.bouncycastle.openpgp.PGPCompressedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedDataList
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPLiteralData
import org.bouncycastle.openpgp.PGPLiteralDataGenerator
import org.bouncycastle.openpgp.PGPObjectFactory
import org.bouncycastle.openpgp.PGPPrivateKey
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPSecretKeyRing
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import java.security.Security
import java.util.Date

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 04/02/2024
 **************************************************************************************************/
internal class CryptoUtils {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    private val fingerprintCalculator = BcKeyFingerprintCalculator()

    @Throws(IOException::class)
    fun getPublicKeyRing(key: ByteArray?): PGPPublicKeyRing {
        val ais = ArmoredInputStream(ByteArrayInputStream(key))
        val pgpObjectFactory = PGPObjectFactory(ais, fingerprintCalculator)
        return pgpObjectFactory.nextObject() as PGPPublicKeyRing
    }

    @Throws(IOException::class)
    fun getSecretKeyRing(key: ByteArray?): PGPSecretKeyRing {
        val keyRingStream = PGPUtil.getDecoderStream(ByteArrayInputStream(key))
        val collection = PGPSecretKeyRingCollection(keyRingStream, fingerprintCalculator)
        return collection.keyRings.next()
    }

    @Throws(Exception::class)
    fun decrypt(
        encryptedText: String?,
        password: String,
        pgpSecretKeyRing: PGPSecretKeyRing
    ): String? {
        return decrypt(
            encryptedText?.toByteArray(),
            password,
            pgpSecretKeyRing
        )?.let { return String(it) }
    }

    @Throws(Exception::class)
    fun decrypt(
        encrypted: ByteArray?,
        password: String,
        pgpSecretKeyRing: PGPSecretKeyRing
    ): ByteArray? {
        var inputStream: InputStream = ByteArrayInputStream(encrypted)
        inputStream = PGPUtil.getDecoderStream(inputStream)
        val pgpF = PGPObjectFactory(inputStream, fingerprintCalculator)
        val enc: PGPEncryptedDataList?
        val o = pgpF.nextObject()
        enc = if (o is PGPEncryptedDataList) {
            o
        } else {
            pgpF.nextObject() as? PGPEncryptedDataList
        }
        var sKey: PGPPrivateKey? = null
        var pbe: PGPPublicKeyEncryptedData? = null
        while (sKey == null && enc?.encryptedDataObjects?.hasNext() == true) {
            pbe = enc.encryptedDataObjects.next() as PGPPublicKeyEncryptedData
            sKey = getPrivateKey(pgpSecretKeyRing, pbe.keyID, password.toCharArray())
        }
        if (pbe != null) {
            val clear = pbe.getDataStream(BcPublicKeyDataDecryptorFactory(sKey))
            var pgpFact = PGPObjectFactory(clear, fingerprintCalculator)
            val cData = pgpFact.nextObject() as PGPCompressedData
            pgpFact = PGPObjectFactory(cData.dataStream, fingerprintCalculator)
            val ld = pgpFact.nextObject() as PGPLiteralData
            val unc = ld.inputStream

            ByteArrayOutputStream().use {
                val buffer = ByteArray(0xFFFF)
                while (true) {
                    val r = unc.read(buffer)
                    if (r == -1) break
                    it.write(buffer, 0, r)
                }
                return it.toByteArray()
            }
        }
        return null
    }

    @Throws(IOException::class, PGPException::class)
    fun encrypt(msgText: String, pgpPublicKeyRing: PGPPublicKeyRing): String? {
        return encrypt(msgText.toByteArray(), pgpPublicKeyRing)?.let { return String(it) }
    }

    @Throws(IOException::class, PGPException::class)
    fun encrypt(msg: ByteArray, publicKeyRing: PGPPublicKeyRing): ByteArray? {
        val encKey = getPublicKey(publicKeyRing)
        val encOut = ByteArrayOutputStream()
        val out = ArmoredOutputStream(encOut)
        val bOut = ByteArrayOutputStream()
        val comData = PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP)
        val cos = comData.open(bOut)
        val lData = PGPLiteralDataGenerator()
        val pOut = lData.open(
            cos,
            PGPLiteralData.BINARY,
            PGPLiteralData.CONSOLE,
            msg.size.toLong(),
            Date()
        )
        pOut.write(msg)
        lData.close()
        comData.close()
        val encGen = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256).setWithIntegrityPacket(true)
                .setSecureRandom(SecureRandom()).setProvider(BouncyCastleProvider.PROVIDER_NAME)
        )
        encGen.addMethod(
            JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider(
                BouncyCastleProvider.PROVIDER_NAME
            )
        )
        val bytes = bOut.toByteArray()
        val cOut = encGen.open(out, bytes.size.toLong())
        cOut.write(bytes)
        cOut.close()
        out.close()
        return encOut.toByteArray()
    }

    @Throws(PGPException::class, IllegalArgumentException::class)
    private fun getPublicKey(publicKeyRing: PGPPublicKeyRing): PGPPublicKey {
        val kIt = publicKeyRing.publicKeys
        while (kIt.hasNext()) {
            val k = kIt.next() as PGPPublicKey
            if (k.isEncryptionKey) {
                return k
            }
        }
        throw IllegalArgumentException("Can't find encryption key in key ring.")
    }

    @Throws(PGPException::class, IllegalArgumentException::class)
    private fun getPrivateKey(
        keyRing: PGPSecretKeyRing,
        keyID: Long,
        pass: CharArray
    ): PGPPrivateKey {
        val pgpSecKey = keyRing.getSecretKey(keyID)
        val decryptor = BcPBESecretKeyDecryptorBuilder(BcPGPDigestCalculatorProvider()).build(pass)
        val secretKey = pgpSecKey?.extractPrivateKey(decryptor)
        if (secretKey != null) {
            return secretKey
        }
        throw IllegalArgumentException(
            "Can't find decryption key in key ring."
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: CryptoUtils? = null

        @JvmStatic
        fun getInstance(): CryptoUtils {
            return INSTANCE ?: synchronized(this) {
                val instance = CryptoUtils()
                INSTANCE = instance
                instance
            }
        }
    }
}
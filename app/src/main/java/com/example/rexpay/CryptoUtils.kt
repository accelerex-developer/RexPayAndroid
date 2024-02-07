@file:JvmSynthetic

package com.example.rexpay

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.didisoft.pgp.PGPLib
import com.google.gson.Gson
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
import org.bouncycastle.openpgp.PGPSecretKeyRing
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory
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
internal class CryptoUtils(private val context: Context) {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    private val pgpLib = PGPLib()

    private val fingerprintCalculator = BcKeyFingerprintCalculator()

    fun encryptPayload(data: String) {
        val dataStream = ByteArrayInputStream(data.toByteArray())
        val keyStream = context.assets.open("rexpay.asc")
        val outputStream = context.openFileOutput("OUTPUT.pgp", MODE_PRIVATE)
        pgpLib.encryptStream(dataStream, "INPUT.txt", keyStream, outputStream, true, false)

        pgpLib.encryptString(data, keyStream)
    }

    fun encryptPayload() {
        val payload = mapOf(
            "reference" to "17072196txazV3zjUH",
            "amount" to "100",
            "customerId" to "random.user@email.com",
            "cardDetails" to mapOf(
                "authDataVersion" to "1",
                "pan" to "5555555555555555",
                "expiryDate" to "1225",
                "cvv2" to "555",
                "pin" to "5555"
            )
        )
        val stringifyJson = Gson().toJson(payload)
        Log.i("CryptoUtils","StringifyJSON: $stringifyJson")
        val data = try {
            encrypt(stringifyJson, rexPayPubKeyRing)
        } catch (e: Exception) {
            Log.e("CryptoUtils", e.message, e)
            null
        }
        Log.i("CryptoUtils","EncryptedPayload:\n$data")
    }

    @Throws(IOException::class)
    fun getPublicKeyRing(key: ByteArray?): PGPPublicKeyRing {
        return ArmoredInputStream(ByteArrayInputStream(key)).use {
            val pgpObjectFactory = PGPObjectFactory(it, fingerprintCalculator)
            pgpObjectFactory.nextObject() as PGPPublicKeyRing
        }
    }

    @Throws(IOException::class)
    fun getSecretKeyRing(key: ByteArray?): PGPSecretKeyRing {
        val keyRingStream = ByteArrayInputStream(key)
        val secretKeyRingCollection = PGPSecretKeyRingCollection(
            PGPUtil.getDecoderStream(keyRingStream),
            fingerprintCalculator
        )
        return secretKeyRingCollection.keyRings.next()
        /*val ais = ArmoredInputStream(ByteArrayInputStream(key))
        return PGPObjectFactory(ais, bcKeyFingerprintCalculator).nextObject() as PGPSecretKeyRing*/
    }

    @Throws(Exception::class)
    fun decrypt(
        encryptedText: String,
        password: String,
        pgpSecretKeyRing: PGPSecretKeyRing
    ): String? {
        return decrypt(
            encryptedText.toByteArray(),
            password,
            pgpSecretKeyRing
        )?.let { return String(it) }
    }

    @Throws(Exception::class)
    fun decrypt(
        encrypted: ByteArray,
        password: String,
        pgpSecretKeyRing: PGPSecretKeyRing
    ): ByteArray? {
        var inputStream: InputStream = ByteArrayInputStream(encrypted)
        inputStream = PGPUtil.getDecoderStream(inputStream)
        val pgpF = PGPObjectFactory(inputStream, fingerprintCalculator)
        val enc: PGPEncryptedDataList
        val o = pgpF.nextObject()
        enc = if (o is PGPEncryptedDataList) {
            o
        } else {
            pgpF.nextObject() as PGPEncryptedDataList
        }
        var sKey: PGPPrivateKey? = null
        var pbe: PGPPublicKeyEncryptedData? = null
        while (sKey == null && enc.encryptedDataObjects.hasNext()) {
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
        val encGen = JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
            .setWithIntegrityPacket(true)
            .setSecureRandom(SecureRandom())
            .setProvider(BouncyCastleProvider.PROVIDER_NAME).let {
                PGPEncryptedDataGenerator(it)
            }
        if (encKey != null) {
            val method = JcePublicKeyKeyEncryptionMethodGenerator(encKey).apply {
                setProvider(BouncyCastleProvider.PROVIDER_NAME)
                setSecureRandom(SecureRandom())
            }
            encGen.addMethod(method)
            val bytes = bOut.toByteArray()
            val cOut = encGen.open(out, bytes.size.toLong())
            cOut.write(bytes)
            cOut.close()
        }
        out.close()
        return encOut.toByteArray()
    }

    @Throws(PGPException::class, IllegalArgumentException::class)
    private fun getPublicKey(publicKeyRing: PGPPublicKeyRing): PGPPublicKey? {
        var masterEncryptionKey: PGPPublicKey? = null
        publicKeyRing.publicKeys.forEach { key ->
            val isMaster = key.isMasterKey
            val isEncryption = key.isEncryptionKey
            if (isMaster && isEncryption) {
                masterEncryptionKey = key
            } else if (!isMaster && isEncryption) {
                return key
            }
        }
        if (masterEncryptionKey != null) {
            return masterEncryptionKey!!
        }
        throw IllegalArgumentException(
            "Can't find encryption key in key ring."
        )
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

    private val rexPayPubKeyRing by lazy {
        val keyArray = getRexPayKey(context)
        getPublicKeyRing(keyArray)
    }

    private fun getRexPayKey(context: Context): ByteArray? {
        return try {
            val buffer = ByteArray(2048)
            val outputStream = ByteArrayOutputStream()
            val stream = context.assets.open("rexpay.asc")

            var length: Int
            while (stream.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CryptoUtils? = null

        @JvmStatic
        fun getInstance(context: Context): CryptoUtils {
            return INSTANCE ?: synchronized(this) {
                val instance = CryptoUtils(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.octacore.rexpay.crypto

import com.octacore.rexpay.crypto.bc.BaseLib
import com.octacore.rexpay.crypto.bc.DirectByteArrayOutputStream
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.Date

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 06/02/2024
 **************************************************************************************************/
internal class PGPLibs {
    fun encrypt(data: String, keyStream: InputStream): String {
        return encrypt(data, keyStream, "UTF-8")
    }

    private fun encrypt(data: String, keyStream: InputStream, var3: String): String {
        var var1 = data
        var var4: ByteArrayInputStream? = null

        try {
            val var5 =
                DirectByteArrayOutputStream(1048576)
            var4 = ByteArrayInputStream(var1.toByteArray(Charset.forName(var3)))
        } finally {

        }
        return var1
    }

    @Throws(PGPException::class, IOException::class)
    fun encryptStream(
        var1: InputStream?,
        var2: String?,
        var3: InputStream?,
        var4: OutputStream?,
        var5: Boolean,
        var6: Boolean
    ) {
        try {
            val var9: PGPPublicKey = this.a(var3)
            this.a(
                var1,
                var2,
                arrayOf<PGPPublicKey>(var9),
                var4,
                Date(),
                var5,
                var6,
                false
            )
        } finally {
            IOUtil.closeStream(var1)
        }
    }

    @Throws(
        IOException::class,
        NoPublicKeyFoundException::class,
        PGPException::class
    )
    private fun a(var1: InputStream): PGPPublicKey? {
        val var6: PGPPublicKeyRingCollection = BaseLib.createPGPPublicKeyRingCollection(var1)
        var var2: PGPException? = null
        var var3: PGPPublicKey? = null
        val var7: Iterator<*> = var6.keyRings
        while (var3 == null && var7.hasNext()) {
            val var4: PGPPublicKeyRing =
                var7.next() as PGPPublicKeyRing
            try {
                var3 = a(var4)
            } catch (var5: PGPException) {
                var2 = var5
            }
            if (var3 != null) {
                break
            }
        }
        return var3
            ?: if (var2 != null) {
                throw var2
            } else {
                throw NoPublicKeyFoundException("Can't find encryption key in key ring.")
            }
    }
}
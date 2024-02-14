@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import android.content.Context
import com.octacore.rexpay.utils.InputOutputUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
class ConfigProp private constructor() {

    private var _username: String = ""

    private var _password: String = ""

    private var _passphrase: String = ""

    private var _publicKey: ByteArray? = null

    private var _privateKey: ByteArray? = null

    private var _rexPayKey: ByteArray? = null

    private var _baseUrl: String = "https://pgs-sandbox.globalaccelerex.com/api/"

    private constructor(
        username: String?,
        password: String?,
        baseUrl: String?,
        publicKey: ByteArray?,
        privateKey: ByteArray?,
        rexPayPubKey: ByteArray?,
        passphrase: String?
    ) : this() {
        if (username == null) throw NullPointerException("API Username cannot be missing")
        this._username = username

        if (password == null) throw NullPointerException("API Password cannot be missing")
        this._password = password

        if (passphrase == null) throw NullPointerException("Passphrase cannot be missing")
        this._passphrase = passphrase

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = publicKey

        this._privateKey = privateKey

        this._rexPayKey = rexPayPubKey
    }

    constructor(
        apiUsername: String?,
        apiPassword: String?,
        baseUrl: String?,
        clientPGPPublicKey: File?,
        clientPGPPrivateKey: File?,
        rexPayPGPPublicKey: File?,
        pgpPassPhrase: String?,
    ) : this() {
        if (apiUsername == null) throw NullPointerException("API Username cannot be missing")
        this._username = apiUsername

        if (apiPassword == null) throw NullPointerException("API Password cannot be missing")
        this._password = apiPassword

        if (pgpPassPhrase == null) throw NullPointerException("Passphrase cannot be missing")
        this._password = apiPassword

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = generateKeyFromFile(clientPGPPublicKey)

        this._privateKey = generateKeyFromFile(clientPGPPrivateKey)

        this._rexPayKey = generateKeyFromFile(rexPayPGPPublicKey)
    }

    constructor(
        apiUsername: String?,
        apiPassword: String?,
        baseUrl: String?,
        clientPGPPublicKey: String?,
        clientPGPPrivateKey: String?,
        rexPayPGPPublicKey: String?,
        pgpPassPhrase: String?,
    ) : this() {
        if (apiUsername == null) throw NullPointerException("API Username cannot be missing")
        this._username = apiUsername

        if (apiPassword == null) throw NullPointerException("API Password cannot be missing")
        this._password = apiPassword

        if (pgpPassPhrase == null) throw NullPointerException("Passphrase cannot be missing")
        this._password = apiPassword

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = clientPGPPublicKey?.toByteArray()

        this._privateKey = clientPGPPrivateKey?.toByteArray()

        this._rexPayKey = rexPayPGPPublicKey?.toByteArray()
    }

    internal val username: String
        get() = _username

    internal val password: String
        get() = _password

    internal val baseUrl: String
        get() = _baseUrl

    internal val publicKey: ByteArray
        get() = _publicKey!!

    internal val privateKey: ByteArray
        get() = _privateKey!!

    internal val rexPayKey: ByteArray
        get() = _rexPayKey!!

    internal val passphrase: String
        get() = _passphrase

    internal fun copy(
        username: String = this._username,
        password: String = this._password,
        baseUrl: String = this._baseUrl,
        publicKey: ByteArray? = this._publicKey,
        privateKey: ByteArray? = this._privateKey,
        rexPayKey: ByteArray? = this._rexPayKey,
        passphrase: String? = this._passphrase,
    ): ConfigProp {
        return ConfigProp(
            username = username,
            password = password,
            baseUrl = baseUrl,
            publicKey = publicKey,
            privateKey = privateKey,
            rexPayPubKey = rexPayKey,
            passphrase = passphrase
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigProp

        if (_username != other._username) return false
        if (_password != other._password) return false
        if (_passphrase != other._passphrase) return false
        if (_publicKey != null) {
            if (other._publicKey == null) return false
            if (!_publicKey.contentEquals(other._publicKey)) return false
        } else if (other._publicKey != null) return false
        if (_privateKey != null) {
            if (other._privateKey == null) return false
            if (!_privateKey.contentEquals(other._privateKey)) return false
        } else if (other._privateKey != null) return false
        if (_rexPayKey != null) {
            if (other._rexPayKey == null) return false
            if (!_rexPayKey.contentEquals(other._rexPayKey)) return false
        } else if (other._rexPayKey != null) return false
        return _baseUrl == other._baseUrl
    }

    override fun hashCode(): Int {
        var result = _username.hashCode()
        result = 31 * result + _password.hashCode()
        result = 31 * result + _passphrase.hashCode()
        result = 31 * result + (_publicKey?.contentHashCode() ?: 0)
        result = 31 * result + (_privateKey?.contentHashCode() ?: 0)
        result = 31 * result + (_rexPayKey?.contentHashCode() ?: 0)
        result = 31 * result + _baseUrl.hashCode()
        return result
    }

    override fun toString(): String {
        return "ConfigProp(" +
                "username='$username', " +
                "password='$password', " +
                "baseUrl='$baseUrl', " +
                "publicKey=${publicKey.contentToString()}, " +
                "privateKey=${privateKey.contentToString()}, " +
                "rexPayKey=${rexPayKey.contentToString()}, " +
                "passphrase='$passphrase'" +
                ")"
    }

    class Builder(context: Context) {
        private var config: ConfigProp = ConfigProp()

        init {
            InputOutputUtils.clearCache(context)
        }

        fun apiUsername(value: String) = apply { config = config.copy(username = value) }

        fun apiPassword(value: String) = apply { config = config.copy(password = value) }

        fun baseUrl(value: String) = apply {
            config = config.copy(baseUrl = value)
        }

        fun clientPGPPublicKey(value: String) = apply {
            config = config.copy(publicKey = value.toByteArray())
        }

        fun clientPGPPublicKey(value: File?) =
            apply { config = config.copy(publicKey = generateKeyFromFile(value)) }

        fun clientPGPPublicKey(value: InputStream?) =
            apply { config = config.copy(publicKey = genKeyFromInputStream(value)) }

        fun clientPGPPrivateKey(value: String) = apply {
            config = config.copy(privateKey = value.toByteArray())
        }

        fun clientPGPPrivateKey(value: File?) =
            apply { config = config.copy(privateKey = generateKeyFromFile(value)) }

        fun clientPGPPrivateKey(value: InputStream?) =
            apply { config = config.copy(privateKey = genKeyFromInputStream(value)) }

        fun rexPayPGPPublicKey(value: String) = apply {
            config = config.copy(rexPayKey = value.toByteArray())
        }

        fun rexPayPGPPublicKey(value: File?) =
            apply { config = config.copy(rexPayKey = generateKeyFromFile(value)) }

        fun rexPayPGPPublicKey(value: InputStream?) =
            apply { config = config.copy(rexPayKey = genKeyFromInputStream(value)) }

        fun passphrase(value: String) = apply { config = config.copy(passphrase = value) }

        fun build(): ConfigProp = config
    }


    companion object {
        private fun genKeyFromInputStream(stream: InputStream?): ByteArray? {
            return stream?.let {
                try {
                    stream.readBytes().also { println(String(it)) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                } finally {
                    stream.close()
                }
            }
        }

        private fun generateKeyFromFile(file: File?): ByteArray? {
            return file?.let {
                try {
                    val buffer = ByteArray(2048)
                    val outputStream = ByteArrayOutputStream()
                    val stream = file.inputStream()

                    var length: Int
                    while (stream.read().also { length = it } != -1) {
                        outputStream.write(buffer, 0, length)
                    }
                    outputStream.toByteArray()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}

@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import android.content.Context
import com.octacore.rexpay.utils.InputOutputUtils
import java.io.ByteArrayOutputStream
import java.io.File

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

    private var _publicKey: ByteArray? = null

    private var _privateKey: ByteArray? = null

    private var _baseUrl: String = "https://pgs-sandbox.globalaccelerex.com/api/"

    private constructor(
        username: String?,
        password: String?,
        baseUrl: String?,
        publicKey: ByteArray?,
        privateKey: ByteArray?
    ) : this() {
        if (username == null) throw NullPointerException("Username cannot be missing")
        this._username = username

        if (password == null) throw NullPointerException("Passphrase cannot be missing")
        this._password = password

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = publicKey

        this._privateKey = privateKey
    }

    constructor(
        username: String?,
        password: String?,
        baseUrl: String?,
        publicKey: File?,
        privateKey: File?
    ) : this() {
        if (username == null) throw NullPointerException("Username cannot be missing")
        this._username = username

        if (password == null) throw NullPointerException("Passphrase cannot be missing")
        this._password = password

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = generateKeyFromFile(publicKey)

        this._privateKey = generateKeyFromFile(privateKey)
    }

    constructor(
        context: Context,
        username: String?,
        password: String?,
        baseUrl: String?,
        publicKey: String?,
        privateKey: String?
    ) : this() {
        InputOutputUtils.clearCache(context)
        if (username == null) throw NullPointerException("Username cannot be missing")
        this._username = username

        if (password == null) throw NullPointerException("Passphrase cannot be missing")
        this._password = password

        this._baseUrl = baseUrl ?: _baseUrl

        this._publicKey = publicKey?.toByteArray()

        this._privateKey = privateKey?.toByteArray()

//        this._publicKey = InputOutputUtils.generateTempFile("pub", publicKey, context)

//        this._privateKey = InputOutputUtils.generateTempFile("sec", privateKey, context)
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

    internal fun copy(
        username: String = this._username,
        password: String = this._password,
        baseUrl: String = this._baseUrl,
        publicKey: ByteArray? = this._publicKey,
        privateKey: ByteArray? = this._privateKey,
    ): ConfigProp {
        return ConfigProp(username, password, baseUrl, publicKey, privateKey)
    }

    class Builder(private val context: Context) {
        private var config: ConfigProp = ConfigProp()

        init {
            InputOutputUtils.clearCache(context)
        }

        fun username(value: String) = apply { config = config.copy(username = value) }

        fun password(value: String) = apply { config = config.copy(password = value) }

        fun baseUrl(value: String) = apply {
            config = config.copy(baseUrl = value)
        }

        fun publicKey(value: String) = apply {
//            val file = InputOutputUtils.generateTempFile("pub", value, context)
            config = config.copy(publicKey = value.toByteArray())
        }

        fun publicKey(value: File?) =
            apply { config = config.copy(publicKey = generateKeyFromFile(value)) }

        fun privateKey(value: String) = apply {
//            val file = InputOutputUtils.generateTempFile("sec", value, context)
            config = config.copy(privateKey = value.toByteArray())
        }

        fun privateKey(value: File?) =
            apply { config = config.copy(privateKey = generateKeyFromFile(value)) }

        fun build(): ConfigProp = config
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigProp

        if (username != other.username) return false
        if (password != other.password) return false
        if (baseUrl != other.baseUrl) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        return privateKey.contentEquals(other.privateKey)
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + baseUrl.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + privateKey.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "ConfigProp(" +
                "username='$username', " +
                "password='$password', " +
                "baseUrl='$baseUrl', " +
                "publicKey=${publicKey.contentToString()}, " +
                "privateKey=${privateKey.contentToString()}" +
                ")"
    }

    companion object {
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

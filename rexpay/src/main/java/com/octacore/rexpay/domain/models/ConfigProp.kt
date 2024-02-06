@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import android.content.Context
import com.octacore.rexpay.utils.InputOutputUtils
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

    private var _publicKey: File? = null

    private var _privateKey: File? = null

    private var _baseUrl: String = "https://pgs-sandbox.globalaccelerex.com/api/"

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

        this._publicKey = publicKey

        this._privateKey = privateKey
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

        this._publicKey = InputOutputUtils.generateTempFile("pub", publicKey, context)

        this._privateKey = InputOutputUtils.generateTempFile("sec", privateKey, context)
    }

    internal val username: String
        get() = _username

    internal val password: String
        get() = _password

    internal val baseUrl: String
        get() = _baseUrl

    internal val publicKey: File
        get() = _publicKey!!

    internal val privateKey: File
        get() = _privateKey!!

    internal fun copy(
        username: String = this._username,
        password: String = this._password,
        baseUrl: String = this._baseUrl,
        publicKey: File? = this._publicKey,
        privateKey: File? = this._privateKey,
    ): ConfigProp {
        return ConfigProp(username, password, baseUrl, publicKey, privateKey)
    }

    override fun toString(): String {
        return "ConfigProp(" +
                "username='$username', " +
                "passphrase='$password', " +
                "baseUrl='$baseUrl'" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigProp

        if (_username != other._username) return false
        if (_password != other._password) return false
        if (_publicKey != other._publicKey) return false
        if (_privateKey != other._privateKey) return false
        return _baseUrl == other._baseUrl
    }

    override fun hashCode(): Int {
        var result = _username.hashCode()
        result = 31 * result + _password.hashCode()
        result = 31 * result + (_publicKey?.hashCode() ?: 0)
        result = 31 * result + (_privateKey?.hashCode() ?: 0)
        result = 31 * result + _baseUrl.hashCode()
        return result
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
            val file = InputOutputUtils.generateTempFile("pub", value, context)
            config = config.copy(publicKey = file)
        }

        fun publicKey(value: File?) = apply { config = config.copy(publicKey = value) }

        fun privateKey(value: String) = apply {
            val file = InputOutputUtils.generateTempFile("sec", value, context)
            config = config.copy(privateKey = file)
        }

        fun privateKey(value: File?) = apply { config = config.copy(privateKey = value) }

        fun build(): ConfigProp = config
    }
}

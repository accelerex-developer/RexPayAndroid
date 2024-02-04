@file:JvmSynthetic

package com.octacore.rexpay.domain.models

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

    private var _passphrase: String = ""

    private var _publicKey: File? = null

    private var _privateKey: File? = null

    private var _baseUrl: String = "https://pgs-sandbox.globalaccelerex.com/api/"

    constructor(username: String?, passphrase: String?, baseUrl: String?) : this() {
        if (username == null) throw NullPointerException("Username cannot be missing")
        this._username = username

        if (passphrase == null) throw NullPointerException("Passphrase cannot be missing")
        this._passphrase = passphrase

        this._baseUrl = baseUrl ?: _baseUrl
    }

    val username: String
        get() = _username

    val passphrase: String
        get() = _passphrase

    val baseUrl: String
        get() = _baseUrl

    internal fun copy(
        username: String = this._username,
        passphrase: String = this._passphrase,
        baseUrl: String = this._baseUrl
    ): ConfigProp {
        return ConfigProp(username, passphrase, baseUrl)
    }

    override fun toString(): String {
        return "ConfigProp(" +
                "username='$username', " +
                "passphrase='$passphrase', " +
                "baseUrl='$baseUrl'" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigProp

        if (_username != other._username) return false
        if (_passphrase != other._passphrase) return false
        if (_publicKey != other._publicKey) return false
        if (_privateKey != other._privateKey) return false
        return _baseUrl == other._baseUrl
    }

    override fun hashCode(): Int {
        var result = _username.hashCode()
        result = 31 * result + _passphrase.hashCode()
        result = 31 * result + (_publicKey?.hashCode() ?: 0)
        result = 31 * result + (_privateKey?.hashCode() ?: 0)
        result = 31 * result + _baseUrl.hashCode()
        return result
    }


    class Builder {
        private var config: ConfigProp = ConfigProp()

        fun username(value: String) = apply { config = config.copy(username = value) }

        fun passphrase(value: String) = apply { config = config.copy(passphrase = value) }

        fun baseUrl(value: String) = apply {
            config = config.copy(baseUrl = value)
        }

        fun publicKey(value: String) = apply { config = config.copy() }

        fun publicKey(value: File?) = apply { config = config.copy() }

        fun privateKey(value: String) = apply { config = config.copy() }

        fun privateKey(value: File?) = apply { config = config.copy() }

        fun build(): ConfigProp = config
    }

}

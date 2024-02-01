@file:JvmSynthetic

package com.octacore.rexpay.data.remote

import com.google.gson.annotations.SerializedName

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal data class ConfigProp(
    @SerializedName("API_USERNAME")
    val username: String,
    @SerializedName("API_PASSPHRASE")
    val passphrase: String,
    @SerializedName("API_URL")
    val baseUrl: String
) {
    companion object {
        val empty = ConfigProp(username = "", passphrase = "", baseUrl = "")
    }
}

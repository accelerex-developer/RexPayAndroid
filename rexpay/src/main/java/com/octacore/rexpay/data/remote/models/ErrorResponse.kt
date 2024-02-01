@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 28/01/2024
 **************************************************************************************************/
internal data class ErrorResponse(
    internal val responseMessage: String? = null,
    internal val responseCode: String? = null,
    internal val responseStatus: String? = null
)

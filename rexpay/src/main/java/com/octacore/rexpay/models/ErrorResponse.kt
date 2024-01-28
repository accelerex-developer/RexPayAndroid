@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 28/01/2024
 **************************************************************************************************/
internal data class ErrorResponse(
    val responseMessage: String? = null,
    val responseCode: String? = null,
)

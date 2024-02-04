@file:JvmSynthetic

package com.octacore.rexpay.data

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/

internal sealed class BaseResult<out T> {
    internal data class Success<out T>(val result: T) : BaseResult<T>()

    internal data class Error(
        val message: String,
        val code: String? = null,
        val status: String? = null
    ) : BaseResult<Nothing>()
}
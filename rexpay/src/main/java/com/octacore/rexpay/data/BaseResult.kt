package com.octacore.rexpay.data

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/

sealed class BaseResult<out T> {
    data class Success<out T>(val result: T) : BaseResult<T>()

    data class Error(val message: String) : BaseResult<Nothing>()
}
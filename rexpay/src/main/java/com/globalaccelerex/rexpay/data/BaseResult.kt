@file:JvmSynthetic

package com.globalaccelerex.rexpay.data

internal sealed class BaseResult<out T> {
    internal data class Success<out T>(val result: T) : BaseResult<T>()

    internal data class Error(
        val message: String,
        val code: String? = null,
        val status: String? = null
    ) : BaseResult<Nothing>()
}
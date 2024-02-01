package com.octacore.rexpay.domain.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/

sealed class PayResult {
    data class Success(
        val status: String?,
    ) : PayResult()

    data class Error(
        val message: String,
        val code: String? = null,
        val status: String? = null,
    ) : PayResult() {
        internal constructor(err: BaseResult.Error?) : this(
            message = err?.message ?: "",
            code = err?.code,
            status = err?.status
        )
    }
}
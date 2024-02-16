package com.octacore.rexpay.domain.models

import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.models.AuthorizeCardResponse

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/

sealed class PayResult {
    data class Success(
        val status: TransactionStatus,
        val amount: String?,
        val responseCode: String? = null,
        val responseDescription: String? = null,
        val paymentId: String? = null,
        val reference: String? = null,
    ) : PayResult() {
        internal constructor(result: AuthorizeCardResponse?) : this(
            status = result?.responseCode.transactionStatus(),
            amount = result?.amount,
            responseCode = result?.responseCode,
            paymentId = result?.paymentId,
            reference = result?.transactionRef,
        )
    }

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

    enum class TransactionStatus {
        SUCCESS,
        PENDING,
        FAILURE,
        UNKNOWN
    }
}

@JvmSynthetic
internal fun String?.transactionStatus(): PayResult.TransactionStatus {
    return when (this) {
        "00" -> PayResult.TransactionStatus.SUCCESS
        else -> PayResult.TransactionStatus.UNKNOWN
    }
}
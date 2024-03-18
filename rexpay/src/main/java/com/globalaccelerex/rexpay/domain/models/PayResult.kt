package com.globalaccelerex.rexpay.domain.models

import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.remote.models.AuthorizeCardResponse
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusResponse
import com.globalaccelerex.rexpay.data.remote.models.UssdPaymentDetailResponse

sealed class PayResult {
    data class Success(
        val status: TransactionStatus,
        val paymentType: PaymentType,
        val amount: String?,
        val responseCode: String? = null,
        val responseDescription: String? = null,
        val paymentId: String? = null,
        val reference: String? = null,
    ) : PayResult() {
        internal constructor(result: AuthorizeCardResponse?) : this(
            paymentType = PaymentType.CARD,
            status = result?.responseCode.transactionStatus(),
            amount = result?.amount,
            responseCode = result?.responseCode,
            paymentId = result?.paymentId,
            reference = result?.transactionRef,
        )

        internal constructor(result: UssdPaymentDetailResponse?) : this(
            paymentType = PaymentType.USSD,
            status = result?.status.transactionStatus(),
            amount = result?.amount?.toString(),
            responseCode = result?.status,
            paymentId = result?.referenceId,
            reference = result?.referenceId
        )

        internal constructor(result: TransactionStatusResponse?) : this(
            paymentType = PaymentType.BANK_TRANSFER,
            status = result?.responseCode.transactionStatus(),
            amount = result?.amount,
            responseCode = result?.responseCode,
            paymentId = result?.paymentReference,
            reference = result?.paymentReference
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
        APPROVED,
        PENDING,
        DECLINED,
        UNKNOWN
    }

    enum class PaymentType {
        CARD,
        USSD,
        BANK_TRANSFER
    }
}

@JvmSynthetic
internal fun String?.transactionStatus(): PayResult.TransactionStatus {
    return when (this) {
        "00", "APPROVED" -> PayResult.TransactionStatus.APPROVED
        else -> PayResult.TransactionStatus.UNKNOWN
    }
}
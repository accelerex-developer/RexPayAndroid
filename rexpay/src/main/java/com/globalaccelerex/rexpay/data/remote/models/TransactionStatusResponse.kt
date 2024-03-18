@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class TransactionStatusResponse(
    internal val amount: String? = null,
    internal val paymentReference: String? = null,
    internal val transactionDate: String? = null,
    internal val currency: String? = null,
    internal val fees: Double? = null,
    internal val channel: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

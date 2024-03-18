@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class ChargeCardResponse(
    internal val paymentId: String? = null,
    internal val amount: String? = null,
    internal val transactionReference: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

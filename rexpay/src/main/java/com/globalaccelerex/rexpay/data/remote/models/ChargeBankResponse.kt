@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class ChargeBankResponse(
    internal val amount: String? = null,
    internal val transactionReference: String? = null,
    internal val accountNumber: String? = null,
    internal val accountName: String? = null,
    internal val bankName: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

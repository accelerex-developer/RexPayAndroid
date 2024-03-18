@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class AuthorizeCardResponse(
    internal val paymentId: String? = null,
    internal val transactionRef: String? = null,
    internal val amount: String? = null,
    internal val responseCode: String? = null
)

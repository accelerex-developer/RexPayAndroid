@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class PaymentCreationResponse(
    internal val reference: String? = null,
    internal val clientId: String? = null,
    internal val paymentUrl: String? = null,
    internal val status: String? = null,
    internal val paymentChannel: String? = null,
    internal val paymentUrlReference: String? = null,
    internal val externalPaymentReference: String? = null,
    internal val fees: Double? = null,
    internal val currency: String? = null,
)

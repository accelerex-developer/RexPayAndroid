@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class UssdPaymentDetailResponse(
    internal val referenceId: String? = null,
    internal val clientId: String? = null,
    internal val userId: String? = null,
    internal val amount: Double? = null,
    internal val fees: Double? = null,
    internal val currency: String? = null,
    internal val createdAt: String? = null,
    internal val channel: String? = null,
    internal val status: String? = null,
    internal val statusMessage: String? = null,
    internal val providerReference: String? = null,
    internal val provider: String? = null,
    internal val providerInitiated: Boolean? = null,
    internal val providerResponse: String? = null,
    internal val paymentUrl: String? = null,
    internal val clientName: String? = null,
    internal val metadata: MetaData? = null,
) {
    internal data class MetaData(
        internal val email: String? = null,
        internal val customerName: String? = null,
    )
}

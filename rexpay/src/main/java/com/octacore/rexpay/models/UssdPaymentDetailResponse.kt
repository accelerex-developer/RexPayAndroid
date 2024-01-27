@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class UssdPaymentDetailResponse(
    val referenceId: String? = null,
    val clientId: String? = null,
    val userId: String? = null,
    val amount: Double? = null,
    val fees: Double? = null,
    val currency: String? = null,
    val createdAt: String? = null,
    val channel: String? = null,
    val status: String? = null,
    val statusMessage: String? = null,
    val providerReference: String? = null,
    val provider: String? = null,
    val providerInitiated: Boolean? = null,
    val providerResponse: String? = null,
    val paymentUrl: String? = null,
    val clientName: String? = null,
    val metadata: MetaData? = null,
) {
    internal data class MetaData(
        val email: String? = null,
        val customerName: String? = null,
    )
}

@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeUssdResponse(
    val reference: String? = null,
    val clientId: String? = null,
    val paymentUrl: String? = null,
    val status: String? = null,
    val paymentChannel: String? = null,
    val providerResponse: String? = null,
    val paymentUrlReference: String? = null,
    val providerExtraInfo: String? = null,
    val externalPaymentReference: String? = null,
    val fees: Double? = null,
    val currency: String? = null,
)

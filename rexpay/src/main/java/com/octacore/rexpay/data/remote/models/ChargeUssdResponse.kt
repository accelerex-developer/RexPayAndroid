@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeUssdResponse(
    internal val reference: String? = null,
    internal val clientId: String? = null,
    internal val paymentUrl: String? = null,
    internal val status: String? = null,
    internal val paymentChannel: String? = null,
    internal val providerResponse: String? = null,
    internal val paymentUrlReference: String? = null,
    internal val providerExtraInfo: String? = null,
    internal val externalPaymentReference: String? = null,
    internal val fees: Double? = null,
    internal val currency: String? = null,
)

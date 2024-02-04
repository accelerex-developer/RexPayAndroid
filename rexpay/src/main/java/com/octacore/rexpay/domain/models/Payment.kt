@file:JvmSynthetic

package com.octacore.rexpay.domain.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 31/01/2024
 **************************************************************************************************/
internal data class Payment(
    internal val reference: String,
    internal val amount: Long,
    internal val currency: String,
    internal val userId: String,
    internal val callbackUrl: String,
    internal val email: String,
    internal val customerName: String,
    internal val clientId: String? = null,
    internal val paymentUrl: String? = null,
    internal val status: String? = null,
    internal val paymentChannel: String? = null,
    internal val paymentUrlReference: String? = null,
    internal val externalPaymentReference: String? = null,
    internal val fees: Double? = null,
)

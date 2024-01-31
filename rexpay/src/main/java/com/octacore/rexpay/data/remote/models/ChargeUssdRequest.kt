@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

import com.octacore.rexpay.data.local.entities.PaymentEntity

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeUssdRequest(
    internal val amount: Long?,
    internal val bankCode: String?,
    internal val clientId: String?,
    internal val currency: String?,
    internal val paymentChannel: String?,
    internal val paymentUrl: String?,
    internal val reference: String?,
    internal val userId: String?,
    internal val callbackUrl: String?,
) {
    constructor(payment: PaymentEntity?, code: String?) : this(
        amount = payment?.amount,
        bankCode = code,
        clientId = payment?.clientId,
        currency = payment?.currency,
        paymentChannel = "USSD",
        paymentUrl = payment?.paymentUrl,
        reference = payment?.reference,
        userId = payment?.userId,
        callbackUrl = payment?.callbackUrl,
    )
}

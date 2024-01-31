@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import com.octacore.rexpay.data.local.entities.PaymentEntity

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
) {
    internal constructor(pay: PaymentEntity?): this(
        reference = pay?.reference ?: "",
        amount = pay?.amount ?: 0L,
        currency = pay?.currency ?: "",
        userId = pay?.userId ?: "",
        callbackUrl = pay?.callbackUrl ?: "",
        email = pay?.email ?: "",
        customerName = pay?.customerName ?: "",
        clientId = pay?.clientId,
        paymentUrl = pay?.paymentUrl,
        status = pay?.status,
        paymentChannel = pay?.paymentChannel,
        paymentUrlReference = pay?.paymentUrlReference,
        externalPaymentReference = pay?.externalPaymentReference,
        fees = pay?.fees
    )
}

@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.data.local.entities.TransactionEntity

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/
internal data class Transaction(
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
    internal constructor(t: TransactionEntity?): this(
        reference = t?.reference ?: "",
        amount = t?.amount ?: 0L,
        currency = t?.currency ?: "",
        userId = t?.userId ?: "",
        callbackUrl = t?.callbackUrl ?: "",
        email = t?.email ?: "",
        customerName = t?.customerName ?: "",
    )
    constructor(t: TransactionEntity?, p: PaymentEntity?) : this(
        reference = t?.reference ?: p?.reference ?: "",
        amount = t?.amount ?: 0L,
        currency = t?.currency ?: "",
        userId = t?.userId ?: "",
        callbackUrl = t?.callbackUrl ?: "",
        email = t?.email ?: "",
        customerName = t?.customerName ?: "",
        clientId = p?.clientId,
        paymentUrl = p?.paymentUrl,
        status = p?.status,
        paymentChannel = p?.paymentChannel,
        paymentUrlReference = p?.paymentUrlReference,
        externalPaymentReference = p?.externalPaymentReference,
        fees = p?.fees
    )
}

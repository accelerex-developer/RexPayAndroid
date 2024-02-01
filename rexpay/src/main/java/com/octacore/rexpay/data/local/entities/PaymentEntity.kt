package com.octacore.rexpay.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.PayPayload

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/
@Entity(tableName = "payment")
internal data class PaymentEntity(
    @PrimaryKey internal val reference: String,
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

    constructor(payload: PayPayload) : this (
        reference = payload.reference,
        amount = payload.amount,
        currency = payload.currency,
        userId = payload.userId,
        callbackUrl = payload.callbackUrl,
        email = payload.email,
        customerName = payload.customerName
    )

    constructor(entity: PaymentEntity?, response: PaymentCreationResponse?) : this(
        reference = entity?.reference ?: "",
        amount = entity?.amount ?: 0L,
        currency = response?.currency ?: entity?.currency ?: "",
        userId = entity?.userId ?: "",
        callbackUrl = entity?.callbackUrl ?: "",
        email = entity?.email ?: "",
        customerName = entity?.customerName ?: "",
        clientId = response?.clientId ?: entity?.clientId ?: "",
        paymentUrl = response?.paymentUrl ?: entity?.paymentUrl ?: "",
        status = response?.status ?: entity?.status ?: "",
        paymentChannel = response?.paymentChannel ?: entity?.paymentChannel ?: "",
        paymentUrlReference = response?.paymentUrlReference ?: entity?.paymentUrlReference ?: "",
        externalPaymentReference = response?.externalPaymentReference
            ?: entity?.externalPaymentReference ?: "",
        fees = response?.fees ?: entity?.fees ?: 0.0
    )

    constructor(entity: PaymentEntity?, response: ChargeUssdResponse?) : this(
        reference = entity?.reference ?: "",
        amount = entity?.amount ?: 0L,
        currency = response?.currency ?: entity?.currency ?: "",
        userId = entity?.userId ?: "",
        callbackUrl = entity?.callbackUrl ?: "",
        email = entity?.email ?: "",
        customerName = entity?.customerName ?: "",
        clientId = response?.clientId ?: entity?.clientId ?: "",
        paymentUrl = response?.paymentUrl ?: entity?.paymentUrl ?: "",
        status = response?.status ?: entity?.status ?: "",
        paymentChannel = response?.paymentChannel ?: entity?.paymentChannel ?: "",
        paymentUrlReference = response?.paymentUrlReference ?: entity?.paymentUrlReference ?: "",
        externalPaymentReference = response?.externalPaymentReference
            ?: entity?.externalPaymentReference ?: "",
        fees = response?.fees ?: entity?.fees ?: 0.0
    )
}
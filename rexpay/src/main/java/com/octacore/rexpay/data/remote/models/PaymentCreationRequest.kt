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
internal data class PaymentCreationRequest(
    internal val reference: String?,
    internal val amount: Long?,
    internal val currency: String?,
    internal val userId: String?,
    internal val callbackUrl: String?,
    internal val metaData: MetaData?,
) {
    internal data class MetaData(
        internal val email: String?,
        internal val customerName: String?,
    )

    internal constructor(data: PaymentEntity?) : this(
        reference = data?.reference,
        amount = data?.amount,
        currency = data?.currency,
        userId = data?.userId,
        callbackUrl = data?.callbackUrl,
        metaData = MetaData(
            email = data?.email,
            customerName = data?.customerName
        )
    )
}
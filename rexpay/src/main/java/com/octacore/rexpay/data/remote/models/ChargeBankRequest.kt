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
internal data class ChargeBankRequest(
    internal val customerName: String?,
    internal val reference: String?,
    internal val amount: String?,
    internal val customerId: String?,
) {
    constructor(payload: PaymentEntity?) : this(
        customerName = payload?.customerName,
        reference = payload?.reference,
        amount = payload?.amount.toString(),
        customerId = payload?.userId
    )
}

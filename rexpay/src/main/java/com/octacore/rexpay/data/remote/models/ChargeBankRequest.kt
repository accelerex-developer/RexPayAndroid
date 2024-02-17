@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

import com.octacore.rexpay.domain.models.Charge

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
    constructor(payload: Charge?, payment: PaymentCreationResponse?): this(
        customerName = payload?.customerName,
        reference = payment?.reference,
        amount = payload?.amount.toString(),
        customerId = payload?.userId
    )
}

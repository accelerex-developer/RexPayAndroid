@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

import com.globalaccelerex.rexpay.domain.models.Charge

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

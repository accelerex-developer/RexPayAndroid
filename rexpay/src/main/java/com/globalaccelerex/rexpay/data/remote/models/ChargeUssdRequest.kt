@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

import com.globalaccelerex.rexpay.domain.models.Charge
import com.globalaccelerex.rexpay.domain.models.USSDBank

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

    constructor(payment: PaymentCreationResponse?, bank: USSDBank?, payload: Charge?): this(
        amount = payload?.amount,
        bankCode = bank?.code,
        clientId = payment?.clientId,
        currency = payment?.currency,
        paymentChannel = "USSD",
        paymentUrl = payment?.paymentUrl,
        reference = payment?.paymentUrlReference,
        userId = payload?.userId,
        callbackUrl = payload?.callbackUrl
    )
}

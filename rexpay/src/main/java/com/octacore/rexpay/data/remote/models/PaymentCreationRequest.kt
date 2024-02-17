@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

import com.google.gson.annotations.SerializedName
import com.octacore.rexpay.domain.models.Charge
import java.util.UUID

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
    @SerializedName("metadata")
    internal val metaData: MetaData?,
) {
    internal data class MetaData(
        internal val email: String?,
        internal val customerName: String?,
    )

    constructor(payload: Charge?) : this(
        reference = UUID.randomUUID().toString().replace(Regex("\\W"), ""),
        amount = payload?.amount,
        currency = payload?.currency,
        userId = payload?.userId,
        callbackUrl = payload?.callbackUrl,
        metaData = MetaData(
            email = payload?.email,
            customerName = payload?.customerName
        )
    )
}
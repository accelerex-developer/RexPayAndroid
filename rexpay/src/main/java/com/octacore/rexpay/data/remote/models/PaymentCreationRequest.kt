@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

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
}
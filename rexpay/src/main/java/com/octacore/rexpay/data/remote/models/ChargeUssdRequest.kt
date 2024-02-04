@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
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
)

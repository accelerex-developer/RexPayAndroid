@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 14/02/2024
 **************************************************************************************************/
internal data class ChargeCardResponse(
    internal val paymentId: String? = null,
    internal val amount: String? = null,
    internal val transactionReference: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

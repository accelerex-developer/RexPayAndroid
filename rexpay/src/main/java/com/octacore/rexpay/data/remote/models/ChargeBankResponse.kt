@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeBankResponse(
    internal val amount: String? = null,
    internal val transactionReference: String? = null,
    internal val accountNumber: String? = null,
    internal val accountName: String? = null,
    internal val bankName: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

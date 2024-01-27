@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeBankResponse(
    val amount: String? = null,
    val transactionReference: String? = null,
    val accountNumber: String? = null,
    val accountName: String? = null,
    val bankName: String? = null,
    val responseCode: String? = null,
    val responseDescription: String? = null,
)

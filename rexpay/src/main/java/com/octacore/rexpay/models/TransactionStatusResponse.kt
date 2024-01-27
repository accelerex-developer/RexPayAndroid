@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class TransactionStatusResponse(
    val amount: String? = null,
    val paymentReference: String? = null,
    val transactionDate: String? = null,
    val currency: String? = null,
    val fees: Double? = null,
    val channel: String? = null,
    val responseCode: String? = null,
    val responseDescription: String? = null,
)

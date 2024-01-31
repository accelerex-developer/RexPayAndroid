@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class TransactionStatusResponse(
    internal val amount: String? = null,
    internal val paymentReference: String? = null,
    internal val transactionDate: String? = null,
    internal val currency: String? = null,
    internal val fees: Double? = null,
    internal val channel: String? = null,
    internal val responseCode: String? = null,
    internal val responseDescription: String? = null,
)

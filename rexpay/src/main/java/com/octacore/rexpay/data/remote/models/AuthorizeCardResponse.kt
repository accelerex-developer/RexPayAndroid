@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 14/02/2024
 **************************************************************************************************/
internal data class AuthorizeCardResponse(
    internal val paymentId: String? = null,
    internal val transactionRef: String? = null,
    internal val amount: String? = null,
    internal val responseCode: String? = null
)

@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class PaymentCreationRequest(
    val reference: String,
    val amount: Long,
    val currency: String,
    val userId: String,
    val callbackUrl: String,
    val metaData: MetaData,
) {
    internal data class MetaData(
        val email: String,
        val customerName: String,
    )
}
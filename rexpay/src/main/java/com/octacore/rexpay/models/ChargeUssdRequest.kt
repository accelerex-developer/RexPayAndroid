@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeUssdRequest(
    val reference: String,
    val userId: String,
    val amount: Long,
    val currency: String,
    val callbackUrl: String,
    val paymentChannel: String,
)

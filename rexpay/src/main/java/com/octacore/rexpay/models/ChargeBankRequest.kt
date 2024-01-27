@file:JvmSynthetic

package com.octacore.rexpay.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeBankRequest(
    val customerName: String,
    val reference: String,
    val amount: String,
    val customerId: String,
)

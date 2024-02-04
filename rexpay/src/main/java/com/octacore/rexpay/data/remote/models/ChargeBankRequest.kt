@file:JvmSynthetic

package com.octacore.rexpay.data.remote.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal data class ChargeBankRequest(
    internal val customerName: String?,
    internal val reference: String?,
    internal val amount: String?,
    internal val customerId: String?,
)

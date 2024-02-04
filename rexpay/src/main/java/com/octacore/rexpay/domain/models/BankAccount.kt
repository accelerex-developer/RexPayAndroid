@file:JvmSynthetic

package com.octacore.rexpay.domain.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal data class BankAccount(
    internal val id: Long,
    internal val bankName: String,
    internal val accountName: String,
    internal val accountNumber: String,
    internal val reference: String,
)

@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import com.octacore.rexpay.data.remote.models.ChargeBankResponse

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal data class BankAccount(
    internal val id: Long = 0L,
    internal val bankName: String,
    internal val accountName: String,
    internal val accountNumber: String,
    internal val reference: String,
) {
    constructor(res: ChargeBankResponse?): this(
        bankName = res?.bankName ?: "",
        accountName = res?.accountName ?: "",
        accountNumber = res?.accountNumber ?: "",
        reference = res?.transactionReference ?: ""
    )
}

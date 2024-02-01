@file:JvmSynthetic

package com.octacore.rexpay.domain.models

import com.octacore.rexpay.data.local.entities.AccountEntity

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
) {
    var payment: Payment? = null
    constructor(entity: AccountEntity?): this(
        id = entity?.id ?: 0L,
        bankName = entity?.bankName ?: "",
        accountName = entity?.accountName ?: "",
        accountNumber = entity?.accountNumber ?: "",
        reference = entity?.transactionReference ?: ""
    )
}

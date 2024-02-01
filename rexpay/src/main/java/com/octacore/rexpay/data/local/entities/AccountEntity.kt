@file:JvmSynthetic

package com.octacore.rexpay.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.octacore.rexpay.data.remote.models.ChargeBankResponse

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
@Entity(tableName = "account")
internal data class AccountEntity(
    @PrimaryKey(autoGenerate = true) internal val id: Long = 0L,
    internal val transactionReference: String,
    internal val accountNumber: String,
    internal val accountName: String,
    internal val bankName: String,
) {
    constructor(res: ChargeBankResponse?) : this(
        transactionReference = res?.transactionReference ?: "",
        accountName = res?.accountName ?: "",
        accountNumber = res?.accountNumber ?: "",
        bankName = res?.bankName ?: "",
    )
}

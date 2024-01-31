package com.octacore.rexpay.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/
@Entity(tableName = "transaction")
data class TransactionEntity(
    @PrimaryKey internal val reference: String,
    internal val amount: Long,
    internal val currency: String,
    internal val userId: String,
    internal val callbackUrl: String,
    internal val email: String,
    internal val customerName: String,
)

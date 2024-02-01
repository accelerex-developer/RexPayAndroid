@file:JvmSynthetic

package com.octacore.rexpay.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal data class PaymentAccountEntity(
    @Embedded internal val payment: PaymentEntity,
    @Relation(
        parentColumn = "reference",
        entityColumn = "transactionReference"
    )
    internal val account: AccountEntity
)

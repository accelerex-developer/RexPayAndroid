@file:JvmSynthetic

package com.octacore.rexpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/

@Dao
internal interface PaymentDao {
    @Insert
    suspend fun insert(item: PaymentEntity)

    @Delete
    suspend fun delete(item: PaymentEntity)

    @Update
    suspend fun update(item: PaymentEntity)

    @Query("SELECT * FROM payment WHERE reference = :reference")
    suspend fun fetchPaymentByRef(reference: String): PaymentEntity

    @Query("SELECT * FROM payment WHERE reference = :reference")
    fun fetchPaymentByRefAsync(reference: String): Flow<PaymentEntity>
}
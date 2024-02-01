@file:JvmSynthetic

package com.octacore.rexpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.octacore.rexpay.data.local.entities.AccountEntity
import com.octacore.rexpay.data.local.entities.PaymentAccountEntity
import com.octacore.rexpay.data.local.entities.PaymentEntity
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
    suspend fun insertPayment(item: PaymentEntity)

    @Insert
    suspend fun insertAccount(item: AccountEntity)

    @Delete
    suspend fun deletePayment(item: PaymentEntity)

    @Delete
    suspend fun deleteAccount(item: AccountEntity)

    @Update
    suspend fun updatePayment(item: PaymentEntity)

    @Update
    suspend fun updateAccount(item: AccountEntity)

    @Query("SELECT * FROM payment WHERE reference = :reference")
    suspend fun fetchPaymentByRef(reference: String): PaymentEntity

    @Query("SELECT * FROM payment WHERE reference = :reference")
    fun fetchPaymentByRefAsync(reference: String): Flow<PaymentEntity>

    @Query("SELECT * FROM payment WHERE reference = :reference")
    suspend fun fetchPaymentAccountByRef(reference: String): PaymentAccountEntity

    @Transaction
    @Query("SELECT * FROM payment WHERE reference = :reference")
    fun fetchPaymentAccountByRefAsync(reference: String): Flow<PaymentAccountEntity>
}
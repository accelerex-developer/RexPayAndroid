@file:JvmSynthetic

package com.octacore.rexpay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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
internal interface TransactionDao {
    @Insert
    suspend fun insert(item: TransactionEntity)

    @Delete
    suspend fun delete(item: TransactionEntity)

    @Query("SELECT * FROM `transaction` WHERE reference = :reference")
    suspend fun fetchTransactionByRef(reference: String): TransactionEntity

    @Query("SELECT * FROM `transaction` WHERE reference = :reference")
    fun fetchTransactionByRefAsync(reference: String): Flow<TransactionEntity>
}
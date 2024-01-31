@file:JvmSynthetic

package com.octacore.rexpay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.octacore.rexpay.data.local.dao.PaymentDao
import com.octacore.rexpay.data.local.dao.TransactionDao
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.data.local.entities.TransactionEntity

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/

@Database(entities = [TransactionEntity::class, PaymentEntity::class], version = 1)
internal abstract class RexPayDb : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
    abstract fun transactionDao(): TransactionDao

    internal companion object {
        @Volatile
        private var INSTANCE: RexPayDb? = null

        internal fun getInstance(context: Context): RexPayDb {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, RexPayDb::class.java, "rexpay-db").build()
                INSTANCE = instance
                instance
            }
        }
    }
}
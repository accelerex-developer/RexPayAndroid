@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.repo.CardTransactionRepoImpl
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.models.Payment
import kotlinx.coroutines.flow.Flow

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface CardTransactionRepo {

    fun getTransaction(reference: String): Flow<Payment>

    companion object {
        @Volatile
        private var INSTANCE: CardTransactionRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService, db: RexPayDb): CardTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = CardTransactionRepoImpl(service, db)
                INSTANCE = instance
                instance
            }
        }
    }
}
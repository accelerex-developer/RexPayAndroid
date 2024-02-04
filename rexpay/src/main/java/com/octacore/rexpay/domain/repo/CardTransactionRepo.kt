@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.repo.CardTransactionRepoImpl

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface CardTransactionRepo {

//    fun getTransaction(reference: String): Flow<Payment>

    companion object {
        @Volatile
        private var INSTANCE: CardTransactionRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService): CardTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = CardTransactionRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
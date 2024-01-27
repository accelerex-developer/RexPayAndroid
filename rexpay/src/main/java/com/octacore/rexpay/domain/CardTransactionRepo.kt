@file:JvmSynthetic

package com.octacore.rexpay.domain

import com.octacore.rexpay.data.CardTransactionRepoImpl
import com.octacore.rexpay.data.PaymentService

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface CardTransactionRepo {

    companion object {
        @Volatile
        private var INSTANCE: CardTransactionRepo? = null

        @JvmStatic
        fun getInstance(): CardTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val service = PaymentService.getInstance()
                val instance = CardTransactionRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
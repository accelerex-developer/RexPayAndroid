@file:JvmSynthetic

package com.octacore.rexpay.data.cache

import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.Charge
import com.octacore.rexpay.domain.models.PayResult

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 03/02/2024
 **************************************************************************************************/


internal interface Cache {

    var hasSession: Boolean?

    var payload: Charge?

    var ussdCode: String?

    var bankAccount: BankAccount?

    var transactionResult: PayResult?

    companion object {
        @Volatile
        private var INSTANCE: Cache? = null

        @JvmStatic
        fun getInstance(): Cache {
            return INSTANCE ?: synchronized(this) {
                val instance = CacheManager()
                INSTANCE = instance
                instance
            }
        }
    }
}
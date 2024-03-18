@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.cache

import com.globalaccelerex.rexpay.domain.models.BankAccount
import com.globalaccelerex.rexpay.domain.models.Charge
import com.globalaccelerex.rexpay.domain.models.PayResult

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
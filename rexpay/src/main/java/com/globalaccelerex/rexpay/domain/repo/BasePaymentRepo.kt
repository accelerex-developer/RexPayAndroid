@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.repo

import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.repo.BasePaymentRepoImpl
import com.globalaccelerex.rexpay.data.BaseResult

internal interface BasePaymentRepo {
    suspend fun initiatePayment(): BaseResult<PaymentCreationResponse?>

    companion object {
        @Volatile
        private var INSTANCE: BasePaymentRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService): BasePaymentRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = BasePaymentRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
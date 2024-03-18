@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.repo

import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.repo.USSDTransactionRepoImpl
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdResponse
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.globalaccelerex.rexpay.domain.models.USSDBank

internal interface USSDTransactionRepo {
    suspend fun chargeUSSD(
        payment: PaymentCreationResponse?,
        bank: USSDBank?
    ): BaseResult<ChargeUssdResponse?>

    suspend fun checkTransactionStatus(reference: String?): BaseResult<UssdPaymentDetailResponse?>

    fun close()

    companion object {
        @Volatile
        private var INSTANCE: USSDTransactionRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService): USSDTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = USSDTransactionRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
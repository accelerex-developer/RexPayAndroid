@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.repo.USSDTransactionRepoImpl
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.octacore.rexpay.domain.models.USSDBank

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
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
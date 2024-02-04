@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.repo.BasePaymentRepoImpl
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import kotlinx.coroutines.flow.Flow

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface BasePaymentRepo {
//    fun getTransaction(reference: String): Flow<Payment>

    suspend fun initiatePayment(reference: String): BaseResult<Payment?>

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
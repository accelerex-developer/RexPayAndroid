@file:JvmSynthetic

package com.octacore.rexpay.domain

import com.octacore.rexpay.data.BasePaymentRepoImpl
import com.octacore.rexpay.data.PaymentService
import com.octacore.rexpay.models.PayPayload

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface BasePaymentRepo {

    suspend fun initiatePayment(request: PayPayload)

    companion object {
        @Volatile
        private var INSTANCE: BasePaymentRepo? = null

        @JvmStatic
        fun getInstance(): BasePaymentRepo {
            return INSTANCE ?: synchronized(this) {
                val service = PaymentService.getInstance()
                val instance = BasePaymentRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.repo.BasePaymentRepoImpl
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface BasePaymentRepo {

    suspend fun initiatePayment(reference: String): BaseResult<Payment?>

    companion object {
        @Volatile
        private var INSTANCE: BasePaymentRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService, database: RexPayDb): BasePaymentRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = BasePaymentRepoImpl(service, database)
                INSTANCE = instance
                instance
            }
        }
    }
}
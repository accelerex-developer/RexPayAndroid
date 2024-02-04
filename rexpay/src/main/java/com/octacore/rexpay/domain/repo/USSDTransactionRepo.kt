@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.repo.USSDTransactionRepoImpl
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.USSDBank

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface USSDTransactionRepo {

//    fun getTransaction(reference: String): Flow<Payment>
    suspend fun chargeUSSD(bank: USSDBank?): BaseResult<String?>

    suspend fun checkTransactionStatus(reference: String)

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
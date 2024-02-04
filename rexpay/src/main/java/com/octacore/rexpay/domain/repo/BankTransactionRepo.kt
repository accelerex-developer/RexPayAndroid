@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.TransactionStatusResponse
import com.octacore.rexpay.data.repo.BankTransactionRepoImpl
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.BaseResult

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface BankTransactionRepo {
    suspend fun initiateBankTransfer(payment: PaymentCreationResponse?): BaseResult<ChargeBankResponse?>
    suspend fun checkTransactionStatus(reference: String?): BaseResult<TransactionStatusResponse?>

    companion object {
        @Volatile
        private var INSTANCE: BankTransactionRepo? = null

        @JvmStatic
        fun getInstance(service: PaymentService): BankTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = BankTransactionRepoImpl(service)
                INSTANCE = instance
                instance
            }
        }
    }
}
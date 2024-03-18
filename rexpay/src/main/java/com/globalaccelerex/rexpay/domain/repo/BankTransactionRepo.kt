@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.repo

import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankResponse
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusResponse
import com.globalaccelerex.rexpay.data.repo.BankTransactionRepoImpl
import com.globalaccelerex.rexpay.data.BaseResult

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
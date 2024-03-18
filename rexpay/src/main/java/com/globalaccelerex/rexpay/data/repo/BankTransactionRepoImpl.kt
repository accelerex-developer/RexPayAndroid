@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.repo

import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankRequest
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankResponse
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusRequest
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusResponse
import com.globalaccelerex.rexpay.domain.models.BankAccount
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.domain.repo.BankTransactionRepo

internal class BankTransactionRepoImpl(private val service: PaymentService) : BankTransactionRepo,
    BaseRepo() {

    private val cache = Cache.getInstance()

    override suspend fun initiateBankTransfer(payment: PaymentCreationResponse?): BaseResult<ChargeBankResponse?> {
        val request = ChargeBankRequest(cache.payload, payment)
        val res = processRequest { service.chargeBank(request) }
        if (res is BaseResult.Success) {
            val account = BankAccount(res.result)
            cache.bankAccount = account
        }
        return res
    }

    override suspend fun checkTransactionStatus(reference: String?): BaseResult<TransactionStatusResponse?> {
        val request = TransactionStatusRequest(reference)
        return processRequest { service.fetchTransactionStatus(request) }.also {
            if (it is BaseResult.Success) {
                cache.transactionResult = PayResult.Success(it.result)
            }
        }
    }
}
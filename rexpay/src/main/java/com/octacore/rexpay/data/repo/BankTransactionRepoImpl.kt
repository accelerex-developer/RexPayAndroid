@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.ChargeBankRequest
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.TransactionStatusRequest
import com.octacore.rexpay.data.remote.models.TransactionStatusResponse
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.domain.models.PayResult
import com.octacore.rexpay.domain.repo.BankTransactionRepo

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 31/01/2024
 **************************************************************************************************/
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
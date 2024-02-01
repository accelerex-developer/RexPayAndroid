@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.local.entities.AccountEntity
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.ChargeBankRequest
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.TransactionStatusRequest
import com.octacore.rexpay.data.remote.models.TransactionStatusResponse
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import com.octacore.rexpay.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 31/01/2024
 **************************************************************************************************/
internal class BankTransactionRepoImpl(
    private val service: PaymentService,
    database: RexPayDb
) : BankTransactionRepo, BaseRepo() {

    private val paymentDao by lazy { database.paymentDao() }

    override fun getAccount(reference: String): Flow<BankAccount> {
        return paymentDao.fetchPaymentAccountByRefAsync(reference).distinctUntilChanged()
            .map {
                val payment = Payment(it.payment)
                BankAccount(it.account).apply {
                    this.payment = payment
                }
            }
    }

    override suspend fun initiateBankTransfer(reference: String): BaseResult<BankAccount?> {
        val payload = withContext(Dispatchers.IO) {
            val data = try {
                paymentDao.fetchPaymentByRef(reference)
            } catch (e: Exception) {
                LogUtils.e("DatabaseError: ${e.message}", e)
                null
            }
            return@withContext data
        }
        val request = ChargeBankRequest(payload)
        return when (val res = processRequest { service.chargeBank(request) }) {
            is BaseResult.Error -> BaseResult.Error(res.message)
            is BaseResult.Success -> {
                val data = withContext(Dispatchers.IO) {
                    val data = res.result?.let { AccountEntity(it) }
                    data?.let { paymentDao.insertAccount(it) }
                    return@withContext BankAccount(data)
                }
                BaseResult.Success(data)
            }
        }
    }

    override suspend fun checkTransactionStatus(reference: String): BaseResult<TransactionStatusResponse?> {
        val request = TransactionStatusRequest(reference)
        return processRequest { service.fetchTransactionStatus(request) }
    }
}
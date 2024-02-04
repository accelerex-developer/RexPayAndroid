@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.ChargeUssdRequest
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.USSDBank
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import com.octacore.rexpay.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/
internal class USSDTransactionRepoImpl(private val service: PaymentService) : USSDTransactionRepo,
    BaseRepo() {

//    private val paymentDao by lazy { database.paymentDao() }

    /*override fun getTransaction(reference: String): Flow<Payment> {
        return paymentDao.fetchPaymentByRefAsync(reference)
            .distinctUntilChanged()
            .map { Payment(it) }
    }*/

    override suspend fun chargeUSSD(bank: USSDBank?): BaseResult<String?> {
        val payload = withContext(Dispatchers.IO) {
            val data = try {
//                paymentDao.fetchPaymentByRef(reference)
            } catch (e: Exception) {
                LogUtils.e("DatabaseError: ${e.message}", e)
                null
            }
            return@withContext data
        }
//        val request = ChargeUssdRequest(payload, bank?.code)
        return when (val res = processRequest { service.chargeUssd(null) }) {
            is BaseResult.Error -> BaseResult.Error(res.message)
            is BaseResult.Success -> {
                /*withContext(Dispatchers.IO) {
                    val paymentEntity = res.result?.let { PaymentEntity(payload, it) }
                    paymentEntity?.let { paymentDao.updatePayment(it) }
                    return@withContext paymentEntity
                }*/
                BaseResult.Success(res.result?.providerResponse)
            }
        }
    }

    override suspend fun checkTransactionStatus(reference: String) {
        TODO("Not yet implemented")
    }

    /*override suspend fun checkTransactionStatus() {
        TODO("Not yet implemented")
    }*/
}
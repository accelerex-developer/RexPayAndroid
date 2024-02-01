@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.PaymentCreationRequest
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.BasePaymentRepo
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
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class BasePaymentRepoImpl(
    private val service: PaymentService,
    database: RexPayDb,
) : BasePaymentRepo, BaseRepo() {

    private val paymentDao by lazy { database.paymentDao() }
    override fun getTransaction(reference: String): Flow<Payment> {
        return paymentDao.fetchPaymentByRefAsync(reference)
            .distinctUntilChanged()
            .map { Payment(it) }
    }

    override suspend fun initiatePayment(reference: String): BaseResult<Payment> {
        val payload = withContext(Dispatchers.IO) {
            val data = try {
                paymentDao.fetchPaymentByRef(reference)
            } catch (e: Exception) {
                LogUtils.e("DatabaseError: ${e.message}", e)
                null
            }
            return@withContext data
        }
        val request = PaymentCreationRequest(payload)
        return when (val res = processRequest { service.createPayment(request) }) {
            is BaseResult.Success -> {
                val data = withContext(Dispatchers.IO) {
                    val data = res.result?.let { PaymentEntity(payload, it) }
                    data?.let { paymentDao.updatePayment(it) }
                    return@withContext data
                }
                BaseResult.Success(Payment(data))
            }

            is BaseResult.Error -> BaseResult.Error(res.message)
        }
    }
}
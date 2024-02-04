@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.models.ChargeUssdRequest
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.UssdPaymentDetailResponse
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
internal class USSDTransactionRepoImpl(
    private val service: PaymentService
) : USSDTransactionRepo, BaseRepo() {

    private val cache by lazy { Cache.getInstance() }

    override suspend fun chargeUSSD(
        payment: PaymentCreationResponse?,
        bank: USSDBank?
    ): BaseResult<ChargeUssdResponse?> {
        val request = ChargeUssdRequest(payment, bank, cache.payload)
        val res = processRequest { service.chargeUssd(request) }
        if (res is BaseResult.Success) {
            cache.ussdCode = res.result?.providerResponse
        }
        return res
    }

    override suspend fun checkTransactionStatus(
        reference: String?,
        clientId: String?
    ): BaseResult<UssdPaymentDetailResponse?> {
        return processRequest { service.fetchUssdPaymentDetail(reference ?: "") }
    }
}
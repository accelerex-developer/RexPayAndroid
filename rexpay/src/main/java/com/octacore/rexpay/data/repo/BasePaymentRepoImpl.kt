@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.PaymentCreationRequest
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.domain.repo.BasePaymentRepo

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class BasePaymentRepoImpl(private val service: PaymentService) : BasePaymentRepo, BaseRepo() {

    private val cache by lazy { Cache.getInstance() }

    override suspend fun initiatePayment(): BaseResult<PaymentCreationResponse?> {
        cache.hasSession = null
        val request = PaymentCreationRequest(cache.payload)
        return processRequest { service.createPayment(request) }
    }
}
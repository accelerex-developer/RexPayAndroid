@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.repo

import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationRequest
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.domain.repo.BasePaymentRepo

internal class BasePaymentRepoImpl(private val service: PaymentService) : BasePaymentRepo, BaseRepo() {

    private val cache by lazy { Cache.getInstance() }

    override suspend fun initiatePayment(): BaseResult<PaymentCreationResponse?> {
        cache.hasSession = null
        val request = PaymentCreationRequest(cache.payload)
        return processRequest { service.createPayment(request) }
    }
}
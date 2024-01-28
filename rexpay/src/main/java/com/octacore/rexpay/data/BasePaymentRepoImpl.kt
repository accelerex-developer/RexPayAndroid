@file:JvmSynthetic

package com.octacore.rexpay.data

import com.octacore.rexpay.domain.BasePaymentRepo
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.models.PaymentCreationRequest
import com.octacore.rexpay.models.PaymentCreationResponse
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class BasePaymentRepoImpl(private val service: PaymentService?) : BasePaymentRepo,
    BaseRepo() {
    override suspend fun initiatePayment(request: PayPayload?): BaseResult<PaymentCreationResponse?> {
        val req = PaymentCreationRequest(
            reference = request?.reference,
            amount = request?.amount,
            currency = request?.currency,
            userId = request?.userId,
            callbackUrl = request?.callbackUrl,
            metaData = PaymentCreationRequest.MetaData(
                email = request?.email,
                customerName = request?.customerName
            )
        )
        LogUtils.i("Transaction started")
        val response = processRequest { service?.createPayment(req) }
        LogUtils.i("Transaction ended: $response")
        return response
    }
}
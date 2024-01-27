@file:JvmSynthetic

package com.octacore.rexpay.data

import com.octacore.rexpay.domain.BasePaymentRepo
import com.octacore.rexpay.models.PayPayload

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class BasePaymentRepoImpl(private val service: PaymentService) : BasePaymentRepo {
    override suspend fun initiatePayment(request: PayPayload) {
        TODO("Not yet implemented")
    }
}
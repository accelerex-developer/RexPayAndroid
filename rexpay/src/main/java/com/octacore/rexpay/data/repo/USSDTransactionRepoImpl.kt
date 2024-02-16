@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import android.os.CountDownTimer
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.ChargeUssdRequest
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.octacore.rexpay.domain.models.PayResult
import com.octacore.rexpay.domain.models.USSDBank
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import com.octacore.rexpay.utils.LogUtils

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

    private var countDownTimer: CountDownTimer? = null

    private var reference: String? = null

    override suspend fun chargeUSSD(
        payment: PaymentCreationResponse?,
        bank: USSDBank?
    ): BaseResult<ChargeUssdResponse?> {
        return if (cache.hasSession == true) {
            BaseResult.Error(message = "USSD transaction currently in progress")
        } else {
            reference = payment?.reference
            val request = ChargeUssdRequest(payment, bank, cache.payload)
            val res = processRequest { service.chargeUssd(request) }
            if (res is BaseResult.Success) {
                cache.hasSession = true
                startCountdown()
                cache.ussdCode = res.result?.providerResponse
            }
            res
        }
    }

    override suspend fun checkTransactionStatus(reference: String?): BaseResult<UssdPaymentDetailResponse?> {
        return processRequest { service.fetchUssdPaymentDetail(reference ?: "") }.also {
            if (it is BaseResult.Success) {
                cache.hasSession = true
                cache.transactionResult = PayResult.Success(it.result)
            }
        }
    }

    override fun close() {
        countDownTimer?.cancel()
    }

    private fun startCountdown() {
        val duration: Long = (30 * 60 * 1000)
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                cache.hasSession = null
            }
        }
        countDownTimer?.start()
    }
}
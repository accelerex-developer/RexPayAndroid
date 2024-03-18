@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.repo

import android.os.CountDownTimer
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdRequest
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdResponse
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.domain.models.USSDBank
import com.globalaccelerex.rexpay.domain.repo.USSDTransactionRepo

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
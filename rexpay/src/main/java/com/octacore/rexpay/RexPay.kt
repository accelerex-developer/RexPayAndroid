package com.octacore.rexpay

import android.app.Application
import android.content.Context
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.RexPayImpl
import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/
interface RexPay {

    fun makePayment(payload: PayPayload)

    fun setPaymentListener(listener: RexPayListener)

    companion object {
        const val PAYMENT_PAYLOAD = "payment_payload"

        private var INSTANCE: RexPay? = null

        private lateinit var context: Application

        @JvmStatic
        fun init(context: Context, showLog: Boolean = BuildConfig.DEBUG) {
            this.context = context.applicationContext as Application
            LogUtils.init(showLog = showLog)
            DI.init(context)
        }

        val instance by lazy {
            INSTANCE ?: synchronized(this) {
                val instance = RexPayImpl(context, DI.database)
                INSTANCE = instance
                instance
            }
        }
    }

    interface RexPayListener {
        fun onSuccess()
        fun onFailure()
    }
}
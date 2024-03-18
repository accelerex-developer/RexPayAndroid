package com.globalaccelerex.rexpay

import android.content.Context
import com.globalaccelerex.rexpay.components.RexPayApp
import com.globalaccelerex.rexpay.data.RexPayImpl
import com.globalaccelerex.rexpay.domain.models.Config
import com.globalaccelerex.rexpay.domain.models.Charge
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.utils.LogUtils

interface RexPay {

    fun makePayment(context: Context, payload: Charge)

    fun setPaymentListener(listener: RexPayListener)

    companion object {
        private var INSTANCE: RexPay? = null

        @JvmStatic
        fun init(
            configProp: Config,
            showLog: Boolean = BuildConfig.DEBUG
        ) {
            LogUtils.init(showLog = showLog)
            RexPayApp.init(configProp)
        }

        fun getInstance(): RexPay {
            return INSTANCE ?: synchronized(this) {
                val instance = RexPayImpl()
                INSTANCE = instance
                instance
            }
        }
    }

    interface RexPayListener {
        fun onResult(result: PayResult?)
    }
}
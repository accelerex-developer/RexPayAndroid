package com.octacore.rexpay

import android.content.Context
import com.octacore.rexpay.data.RexPayImpl
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.domain.models.PayResult
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/
interface RexPay {

    fun makePayment(context: Context, payload: PayPayload)

    fun setPaymentListener(listener: RexPayListener)

    companion object {
        private var INSTANCE: RexPay? = null

        @JvmStatic
        fun init(
            context: Context,
            configProp: ConfigProp,
            showLog: Boolean = BuildConfig.DEBUG
        ) {
            RexPayApp.init(context, configProp)
            LogUtils.init(showLog = showLog)
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
        fun onResult(result: PayResult)
    }
}
package com.octacore.rexpay

import android.content.Context
import com.octacore.rexpay.data.RexPayImpl
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

    fun makePayment(payload: PayPayload)

    fun setPaymentListener(listener: RexPayListener)

    companion object {
        private var INSTANCE: RexPay? = null

        @JvmStatic
        fun getInstance(context: Context, showLog: Boolean = BuildConfig.DEBUG): RexPay {
            return INSTANCE ?: synchronized(this) {
                RexPayApp.init(context)
                LogUtils.init(showLog = showLog)
                val instance = RexPayImpl(context, RexPayApp.database)
                INSTANCE = instance
                instance
            }
        }
    }

    interface RexPayListener {
        fun onResult(result: PayResult)
    }
}
package com.octacore.rexpay

import android.content.Context
import com.octacore.rexpay.data.RexPayImpl
import com.octacore.rexpay.models.PayPayload

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

        @JvmStatic
        fun getInstance(context: Context): RexPay {
            return INSTANCE ?: synchronized(this) {
                val instance = RexPayImpl(context)
                INSTANCE = instance
                instance
            }
        }

        internal fun getInstance(): RexPay? = INSTANCE
    }

    interface RexPayListener {
        fun onSuccess()
        fun onFailure()
    }
}
@file:JvmSynthetic

package com.octacore.rexpay.data

import android.content.Context
import android.content.Intent
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.RexPay.Companion.PAYMENT_PAYLOAD
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.PaymentActivity
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/
internal class RexPayImpl(private val context: Context) : RexPay {
    private var listener: RexPay.RexPayListener? = null

    override fun makePayment(payload: PayPayload) {
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(PAYMENT_PAYLOAD, payload)
        context.startActivity(intent)
    }

    override fun setPaymentListener(listener: RexPay.RexPayListener) {
        this.listener = listener
    }
}
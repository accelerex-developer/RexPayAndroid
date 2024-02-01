package com.octacore.rexpay.components

import android.content.Context
import android.content.Intent
import com.octacore.rexpay.components.PaymentManager.Companion.PAYMENT_PAYLOAD
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.domain.models.PayResult
import com.octacore.rexpay.ui.PaymentActivity

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal class PaymentManagerImpl : PaymentManager {

    private var listener: PaymentManager.Listener? = null

    override fun startActivity(context: Context, payload: PayPayload) {
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(PAYMENT_PAYLOAD, payload)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun onResponse(result: PayResult) {
        listener?.onResult(result)
    }

    override fun setOnResultListener(listener: PaymentManager.Listener) {
        this.listener = listener
    }
}
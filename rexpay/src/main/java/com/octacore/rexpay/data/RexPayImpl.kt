@file:JvmSynthetic

package com.octacore.rexpay.data

import android.content.Context
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.components.PaymentManager
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.domain.models.Charge
import com.octacore.rexpay.domain.models.PayResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/
internal class RexPayImpl : RexPay, PaymentManager.Listener {

    private var listener: RexPay.RexPayListener? = null

    override fun makePayment(context: Context, payload: Charge) {
        val cache = Cache.getInstance()
        cache.payload = payload
        CoroutineScope(Dispatchers.Main).launch {
            val manager = PaymentManager.getInstance()
            manager.setOnResultListener(this@RexPayImpl)
            manager.startActivity(context)
        }
    }

    override fun setPaymentListener(listener: RexPay.RexPayListener) {
        this.listener = listener
    }

    override fun onResult(result: PayResult?) {
        listener?.onResult(result)
    }
}
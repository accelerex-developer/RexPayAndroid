@file:JvmSynthetic

package com.globalaccelerex.rexpay.data

import android.content.Context
import com.globalaccelerex.rexpay.RexPay
import com.globalaccelerex.rexpay.components.PaymentManager
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.domain.models.Charge
import com.globalaccelerex.rexpay.domain.models.PayResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
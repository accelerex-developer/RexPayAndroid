package com.globalaccelerex.rexpay.components

import android.content.Context
import android.content.Intent
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.utils.getActivity

internal class PaymentManagerImpl : PaymentManager {

    private var listener: PaymentManager.Listener? = null

    override fun startActivity(context: Context) {
        val intent = Intent(context, RexPayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun onResponse(context: Context, result: PayResult?) {
        listener?.onResult(result)
        context.getActivity()?.finishAfterTransition()
    }

    override fun setOnResultListener(listener: PaymentManager.Listener) {
        this.listener = listener
    }
}
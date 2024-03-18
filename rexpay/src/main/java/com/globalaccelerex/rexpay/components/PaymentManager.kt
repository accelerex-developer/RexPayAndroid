@file:JvmSynthetic

package com.globalaccelerex.rexpay.components

import android.content.Context
import com.globalaccelerex.rexpay.domain.models.PayResult

internal interface PaymentManager {
    fun startActivity(context: Context)

    fun onResponse(context: Context, result: PayResult?)

    fun setOnResultListener(listener: Listener)

    interface Listener {
        fun onResult(result: PayResult?)
    }

    companion object {
        @Volatile
        private var INSTANCE: PaymentManager? = null

        @JvmStatic
        fun getInstance(): PaymentManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PaymentManagerImpl()
                INSTANCE = instance
                instance
            }
        }
    }
}
@file:JvmSynthetic

package com.octacore.rexpay.components

import android.content.Context
import com.octacore.rexpay.domain.models.PayResult

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal interface PaymentManager {
    fun startActivity(context: Context)

    fun onResponse(result: PayResult)

    fun setOnResultListener(listener: Listener)

    interface Listener {
        fun onResult(result: PayResult)
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
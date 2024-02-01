@file:JvmSynthetic

package com.octacore.rexpay.data

import android.content.Context
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.components.PaymentManager
import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.domain.models.PayResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/
internal class RexPayImpl(
    private val context: Context,
    private val database: RexPayDb
) : RexPay, PaymentManager.Listener {

    private var listener: RexPay.RexPayListener? = null

    private val paymentDao by lazy { database.paymentDao() }

    override fun makePayment(payload: PayPayload) {
        CoroutineScope(Dispatchers.Main).launch {
            val manager = PaymentManager.getInstance()
            withContext(Dispatchers.IO) {
                val entity = PaymentEntity(payload)
                paymentDao.insertPayment(entity)
            }
            manager.setOnResultListener(this@RexPayImpl)
            manager.startActivity(context, payload)
        }
    }

    override fun setPaymentListener(listener: RexPay.RexPayListener) {
        this.listener = listener
    }

    override fun onResult(result: PayResult) {
        listener?.onResult(result)
        database.clearAllTables()
        database.close()
    }
}
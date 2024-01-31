@file:JvmSynthetic

package com.octacore.rexpay.data

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.RexPay.Companion.PAYMENT_PAYLOAD
import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.local.entities.PaymentEntity
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.ui.PaymentActivity
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
    database: RexPayDb
) : RexPay {
    private var listener: RexPay.RexPayListener? = null

    private val paymentDao by lazy { database.paymentDao() }

    override fun makePayment(payload: PayPayload) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val entity = PaymentEntity(payload)
                paymentDao.insert(entity)
            }
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(PAYMENT_PAYLOAD, payload)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun setPaymentListener(listener: RexPay.RexPayListener) {
        this.listener = listener
    }
}
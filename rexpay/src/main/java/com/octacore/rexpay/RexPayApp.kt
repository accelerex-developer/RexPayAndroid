@file:JvmSynthetic

package com.octacore.rexpay

import android.app.Application
import android.content.Context
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 31/01/2024
 **************************************************************************************************/
internal object RexPayApp {
    private lateinit var context: Application

    private lateinit var config: ConfigProp

    @JvmStatic
    fun init(context: Context, config: ConfigProp) {
        this.context = context.applicationContext as Application
        this.config = config
    }

    private val service by lazy { PaymentService.getInstance(context, config) }

    val basePaymentRepo by lazy { BasePaymentRepo.getInstance(service) }

    val ussdRepo by lazy { USSDTransactionRepo.getInstance(service) }

    val cardRepo by lazy { CardTransactionRepo.getInstance(service) }

    val bankRepo by lazy { BankTransactionRepo.getInstance(service) }
}
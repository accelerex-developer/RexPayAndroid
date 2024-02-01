@file:JvmSynthetic

package com.octacore.rexpay

import android.app.Application
import android.content.Context
import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.domain.repo.USSDTransactionRepo

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 31/01/2024
 **************************************************************************************************/
internal object RexPayApp {
    private lateinit var context: Application

    @JvmStatic
    fun init(context: Context) {
        this.context = context.applicationContext as Application
    }

    private val service by lazy { PaymentService.getInstance(context) }

    val database by lazy { RexPayDb.getInstance(context) }

    val basePaymentRepo by lazy { BasePaymentRepo.getInstance(service, database) }

    val ussdRepo by lazy { USSDTransactionRepo.getInstance(service, database) }

    val cardRepo by lazy { CardTransactionRepo.getInstance(service, database) }

    val bankRepo by lazy { BankTransactionRepo.getInstance(service, database) }
}
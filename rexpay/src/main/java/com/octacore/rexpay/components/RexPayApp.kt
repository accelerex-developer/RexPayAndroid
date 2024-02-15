@file:JvmSynthetic

package com.octacore.rexpay.components

import android.app.Application
import android.content.Context
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.models.ConfigProp
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

    private var _baseRepo: BasePaymentRepo? = null

    private var _ussdRepo: USSDTransactionRepo? = null

    private var _cardRepo: CardTransactionRepo? = null

    private var _bankRepo: BankTransactionRepo? = null

    @JvmStatic
    internal fun init(context: Context, config: ConfigProp) {
        val app = context.applicationContext as Application

        val service = PaymentService.getInstance(config)
        _baseRepo = BasePaymentRepo.getInstance(service)
        _ussdRepo = USSDTransactionRepo.getInstance(service)
        _cardRepo = CardTransactionRepo.getInstance(service, config)
        _bankRepo = BankTransactionRepo.getInstance(service)
    }

    internal val basePaymentRepo: BasePaymentRepo
        get() = _baseRepo!!

    internal val ussdRepo: USSDTransactionRepo
        get() = _ussdRepo!!

    internal val cardRepo: CardTransactionRepo
        get() = _cardRepo!!

    internal val bankRepo: BankTransactionRepo
        get() = _bankRepo!!
}
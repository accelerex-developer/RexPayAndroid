@file:JvmSynthetic

package com.globalaccelerex.rexpay.components

import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.domain.models.Config
import com.globalaccelerex.rexpay.domain.repo.BankTransactionRepo
import com.globalaccelerex.rexpay.domain.repo.BasePaymentRepo
import com.globalaccelerex.rexpay.domain.repo.CardTransactionRepo
import com.globalaccelerex.rexpay.domain.repo.USSDTransactionRepo

internal object RexPayApp {

    private var _baseRepo: BasePaymentRepo? = null

    private var _ussdRepo: USSDTransactionRepo? = null

    private var _cardRepo: CardTransactionRepo? = null

    private var _bankRepo: BankTransactionRepo? = null

    @JvmStatic
    internal fun init(config: Config) {
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
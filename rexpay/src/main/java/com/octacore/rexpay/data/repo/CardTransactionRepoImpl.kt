@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.repo.CardTransactionRepo

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class CardTransactionRepoImpl(private val service: PaymentService) : CardTransactionRepo, BaseRepo() {

//    private val paymentDao by lazy { database.paymentDao() }

//    private val transactionDao by lazy { database.transactionDao() }

    /*override fun getTransaction(reference: String): Flow<Payment> {
        return paymentDao.fetchPaymentByRefAsync(reference)
            .distinctUntilChanged()
            .map { Payment(it) }
    }*/
}
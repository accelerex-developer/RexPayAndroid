@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.octacore.rexpay.data.local.RexPayDb
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class CardTransactionRepoImpl(
    private val service: PaymentService,
    database: RexPayDb,
) : CardTransactionRepo, BaseRepo() {

    private val paymentDao by lazy { database.paymentDao() }

//    private val transactionDao by lazy { database.transactionDao() }

    override fun getTransaction(reference: String): Flow<Payment> {
        return paymentDao.fetchPaymentByRefAsync(reference)
            .distinctUntilChanged()
            .map { Payment(it) }
    }
}
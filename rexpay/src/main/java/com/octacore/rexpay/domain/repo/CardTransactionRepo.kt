@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.AuthorizeCardResponse
import com.octacore.rexpay.data.remote.models.ChargeCardResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.repo.CardTransactionRepoImpl
import com.octacore.rexpay.domain.models.CardDetail
import com.octacore.rexpay.domain.models.Config

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface CardTransactionRepo {

    suspend fun chargeCard(
        card: CardDetail,
        payment: PaymentCreationResponse?
    ): BaseResult<ChargeCardResponse?>

    suspend fun authorizeTransaction(pin: String): BaseResult<AuthorizeCardResponse?>

    companion object {
        const val REX_PAY_KEY = "RexPayPublicKey"

        @Volatile
        private var INSTANCE: CardTransactionRepo? = null

        @JvmStatic
        fun getInstance(
            service: PaymentService,
            config: Config,
        ): CardTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = CardTransactionRepoImpl(service, config)
                INSTANCE = instance
                instance
            }
        }
    }
}
@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.repo

import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.remote.PaymentService
import com.globalaccelerex.rexpay.data.remote.models.AuthorizeCardResponse
import com.globalaccelerex.rexpay.data.remote.models.ChargeCardResponse
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.repo.CardTransactionRepoImpl
import com.globalaccelerex.rexpay.domain.models.CardDetail
import com.globalaccelerex.rexpay.domain.models.Config

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
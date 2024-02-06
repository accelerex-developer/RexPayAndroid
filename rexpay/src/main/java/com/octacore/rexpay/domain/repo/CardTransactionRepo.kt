@file:JvmSynthetic

package com.octacore.rexpay.domain.repo

import android.content.Context
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.repo.CardTransactionRepoImpl
import com.octacore.rexpay.domain.models.CardDetail
import com.octacore.rexpay.domain.models.ConfigProp

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface CardTransactionRepo {

    suspend fun chargeCard(card: CardDetail, payment: PaymentCreationResponse?): BaseResult<String?>

    companion object {
        const val REX_PAY_KEY = "RexPayPublicKey"

        @Volatile
        private var INSTANCE: CardTransactionRepo? = null

        @JvmStatic
        fun getInstance(
            context: Context,
            service: PaymentService,
            config: ConfigProp,
        ): CardTransactionRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = CardTransactionRepoImpl(context, service, config)
                INSTANCE = instance
                instance
            }
        }
    }
}
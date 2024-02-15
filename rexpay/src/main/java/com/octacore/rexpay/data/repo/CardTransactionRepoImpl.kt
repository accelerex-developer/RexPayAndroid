@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.AuthorizeCardResponse
import com.octacore.rexpay.data.remote.models.ChargeCardResponse
import com.octacore.rexpay.data.remote.models.EncryptedRequest
import com.octacore.rexpay.data.remote.models.KeyRequest
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.CardDetail
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.utils.CryptoUtils
import com.octacore.rexpay.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class CardTransactionRepoImpl(
    private val service: PaymentService,
    private val config: ConfigProp,
) : CardTransactionRepo, BaseRepo() {

    private val cache by lazy { Cache.getInstance() }

    private val crypto by lazy { CryptoUtils.getInstance() }

    private val clientSecKeyRing by lazy {
        crypto.getSecretKeyRing(config.privateKey)
    }

    private val rexPayPubKeyRing by lazy {
        crypto.getPublicKeyRing(config.rexPayKey)
    }

    private var chargeResponse by mutableStateOf<ChargeCardResponse?>(null)

    override suspend fun chargeCard(
        card: CardDetail,
        payment: PaymentCreationResponse?
    ): BaseResult<ChargeCardResponse?> = withContext(Dispatchers.IO) {
        val result = insertPublicKey(payment?.clientId)
        if (result is BaseResult.Success) {
            val payload = mapOf(
                "reference" to payment?.reference,
                "amount" to cache.payload?.amount.toString(),
                "customerId" to cache.payload?.userId,
                "cardDetails" to mapOf(
                    "authDataVersion" to "1",
                    "pan" to card.pan.trim().replace(" ", ""),
                    "expiryDate" to card.expiryDate.replace("/", "").trim(),
                    "cvv2" to card.cvv2.trim(),
                    "pin" to card.pin.trim()
                )
            )
            val res = chargeCard(payload)
            if (res is BaseResult.Success) {
                chargeResponse = res.result
                LogUtils.i(chargeResponse.toString())
            }
            return@withContext res
        }
        val error = result as BaseResult.Error
        return@withContext BaseResult.Error(error.message)
    }

    override suspend fun authorizeTransaction(pin: String): BaseResult<AuthorizeCardResponse?> =
        withContext(Dispatchers.IO) {
            val payload = mapOf(
                "paymentId" to chargeResponse?.paymentId,
                "otp" to pin
            )
            val stringifyJson = Gson().toJson(payload)
            val data = try {
                crypto.encrypt(stringifyJson, rexPayPubKeyRing)
            } catch (e: Exception) {
                LogUtils.e(e.message, e)
                null
            }
            val res = processRequest { service.authorizeTransaction(EncryptedRequest(data)) }
            if (res is BaseResult.Success) {
                val response = res.result
                val decryptedString =
                    crypto.decrypt(response?.encryptedResponse, config.passphrase, clientSecKeyRing)
                return@withContext BaseResult.Success(
                    Gson().fromJson(
                        decryptedString,
                        AuthorizeCardResponse::class.java
                    )
                )
            }
            val error = res as BaseResult.Error
            return@withContext BaseResult.Error(error.message)
        }

    private suspend fun insertPublicKey(clientId: String?) = withContext(Dispatchers.IO) {
        val request = KeyRequest(
            clientId = clientId,
            publicKey = String(config.publicKey)
        )
        processRequest { service.insertPublicKey(request) }
    }

    private suspend fun chargeCard(payload: Map<String, Any?>?): BaseResult<ChargeCardResponse?> {
        val stringifyJson = Gson().toJson(payload)
        val data = try {
            crypto.encrypt(stringifyJson, rexPayPubKeyRing)
        } catch (e: Exception) {
            LogUtils.e(e.message, e)
            null
        }
        val response = processRequest { service.chargeCard(EncryptedRequest(data)) }
        if (response is BaseResult.Success) {
            val decrypted = crypto.decrypt(
                response.result?.encryptedResponse,
                config.passphrase,
                clientSecKeyRing
            )
            return BaseResult.Success(Gson().fromJson(decrypted, ChargeCardResponse::class.java))
        }
        val error = response as? BaseResult.Error
        return BaseResult.Error(error?.message ?: "An error occurred!")
    }
}
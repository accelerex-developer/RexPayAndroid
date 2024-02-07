@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import android.content.Context
import com.google.gson.Gson
import com.octacore.rexpay.BuildConfig
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.cache.Cache
import com.octacore.rexpay.data.remote.PaymentService
import com.octacore.rexpay.data.remote.models.EncryptedRequest
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.CardDetail
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.utils.CryptoUtils
import com.octacore.rexpay.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class CardTransactionRepoImpl(
    private val context: Context,
    private val service: PaymentService,
    private val config: ConfigProp,
) : CardTransactionRepo, BaseRepo() {

    private val cache by lazy { Cache.getInstance() }

    private val crypto by lazy { CryptoUtils.getInstance() }

    private val clientPubKeyRing by lazy {
//        val keyArray = generateKeyFromFile(config.publicKey)
        crypto.getPublicKeyRing(config.publicKey)
    }

    private val clientSecKeyRing by lazy {
//        val keyArray = generateKeyFromFile(config.privateKey)
        crypto.getSecretKeyRing(config.privateKey)
    }

    private val rexPayPubKeyRing by lazy {
        val keyArray = getRexPayKey(context)
        crypto.getPublicKeyRing(keyArray)
    }

    override suspend fun chargeCard(
        card: CardDetail,
        payment: PaymentCreationResponse?
    ): BaseResult<String?> = withContext(Dispatchers.IO) {
        val payload = if (BuildConfig.DEBUG && card.pan.isEmpty()) mapOf(
            "reference" to payment?.paymentUrlReference,
            "amount" to cache.payload?.amount.toString(),
            "customerId" to cache.payload?.userId,
            "cardDetails" to mapOf(
                "authDataVersion" to "1",
                "pan" to "5555555555555555",
                "expiryDate" to "1225",
                "cvv2" to "555",
                "pin" to "5555"
            )
        ) else mapOf(
            "reference" to payment?.paymentUrlReference,
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
        /*val payload = mapOf(
            "reference" to payment?.paymentUrlReference,
            "amount" to cache.payload?.amount.toString(),
            "customerId" to cache.payload?.userId,
            "cardDetails" to mapOf(
                "authDataVersion" to "1",
                "pan" to card.pan.trim().replace(" ", ""),
                "expiryDate" to card.expiryDate.replace("/", "").trim(),
                "cvv2" to card.cvv2.trim(),
                "pin" to card.pin.trim()
            )
        )*/
        val stringifyJson = Gson().toJson(payload)
        LogUtils.i("StringifyJSON: $stringifyJson")
        val data = try {
            crypto.encrypt(stringifyJson, rexPayPubKeyRing)
        } catch (e: Exception) {
            LogUtils.e(e.message, e)
            null
        }
        LogUtils.i("EncryptedPayload: $data")
        val response = processRequest { service.chargeCard(EncryptedRequest(data)) }
        LogUtils.i("ChargeCardResponse: $response")
        return@withContext BaseResult.Success("")
    }

    private fun generateKeyFromFile(file: File?): ByteArray? {
        return file?.let {
            try {
                val buffer = ByteArray(2048)
                val outputStream = ByteArrayOutputStream()
                val stream = file.inputStream()

                var length: Int
                while (stream.read().also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun getRexPayKey(context: Context): ByteArray? {
        return try {
            val buffer = ByteArray(1 shl 16)
            val outputStream = ByteArrayOutputStream()
            val stream = context.assets.open("rexpay.asc")

            var length: Int
            while (stream.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
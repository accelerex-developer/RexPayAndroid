@file:JvmSynthetic

package com.octacore.rexpay.data

import com.google.gson.GsonBuilder
import com.octacore.rexpay.models.ChargeBankRequest
import com.octacore.rexpay.models.ChargeBankResponse
import com.octacore.rexpay.models.ChargeUssdRequest
import com.octacore.rexpay.models.ChargeUssdResponse
import com.octacore.rexpay.models.EncryptedRequest
import com.octacore.rexpay.models.EncryptedResponse
import com.octacore.rexpay.models.KeyRequest
import com.octacore.rexpay.models.PaymentCreationRequest
import com.octacore.rexpay.models.PaymentCreationResponse
import com.octacore.rexpay.models.TransactionStatusRequest
import com.octacore.rexpay.models.TransactionStatusResponse
import com.octacore.rexpay.models.UssdPaymentDetailResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface PaymentService {
    @POST("/pgs/payment/v2/createPayment")
    suspend fun createPayment(request: PaymentCreationRequest): Response<PaymentCreationResponse>

    @POST("/cps/v1/chargeCard")
    suspend fun chargeCard(request: EncryptedRequest): Response<EncryptedResponse>

    @POST("/cps/v1/authorizeTransaction")
    suspend fun authorizeTransaction(request: EncryptedRequest): Response<EncryptedResponse>

    @POST("/cps/v1/initiateBankTransfer")
    suspend fun chargeBank(request: ChargeBankRequest): Response<ChargeBankResponse>

    @POST("/cps/v1/getTransactionStatus")
    suspend fun fetchTransactionStatus(request: TransactionStatusRequest): Response<TransactionStatusResponse>

    @POST("/pgs/payment/v1/makePayment")
    suspend fun chargeUssd(request: ChargeUssdRequest): Response<ChargeUssdResponse>

    @GET("/pgs/payment/v1/getPaymentDetails/{TransRef}")
    suspend fun fetchUssdPaymentDetail(@Path("TransRef") reference: String): Response<UssdPaymentDetailResponse>

    @POST("/pgs/clients/v1/publicKey")
    suspend fun insertPublicKey(request: KeyRequest): Response<*>

    companion object {
        @Volatile
        private var INSTANCE: PaymentService? = null

        @JvmStatic
        fun getInstance(): PaymentService {
            return INSTANCE ?: synchronized(this) {
                val gson = GsonBuilder()
                    .setLenient()
                    .setPrettyPrinting()
                    .create()
                val instance = Retrofit.Builder()
                    .baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create<PaymentService>()
                INSTANCE = instance
                instance
            }
        }
    }
}
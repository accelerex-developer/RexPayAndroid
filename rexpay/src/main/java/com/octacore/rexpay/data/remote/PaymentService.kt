@file:JvmSynthetic

package com.octacore.rexpay.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.octacore.rexpay.data.remote.models.ChargeBankRequest
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.ChargeUssdRequest
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.EncryptedRequest
import com.octacore.rexpay.data.remote.models.EncryptedResponse
import com.octacore.rexpay.data.remote.models.KeyRequest
import com.octacore.rexpay.data.remote.models.PaymentCreationRequest
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.data.remote.models.TransactionStatusRequest
import com.octacore.rexpay.data.remote.models.TransactionStatusResponse
import com.octacore.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.octacore.rexpay.domain.models.ConfigProp
import com.octacore.rexpay.utils.AuthInterceptor
import com.octacore.rexpay.utils.LogUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.File
import java.util.concurrent.TimeUnit

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal interface PaymentService {
    @POST("pgs/payment/v2/createPayment")
    suspend fun createPayment(@Body request: PaymentCreationRequest): Response<PaymentCreationResponse?>

    @POST("cps/v1/chargeCard")
    suspend fun chargeCard(@Body request: EncryptedRequest): Response<EncryptedResponse?>

    @POST("cps/v1/authorizeTransaction")
    suspend fun authorizeTransaction(@Body request: EncryptedRequest): Response<EncryptedResponse?>

    @POST("cps/v1/initiateBankTransfer")
    suspend fun chargeBank(@Body request: ChargeBankRequest): Response<ChargeBankResponse?>

    @POST("cps/v1/getTransactionStatus")
    suspend fun fetchTransactionStatus(@Body request: TransactionStatusRequest): Response<TransactionStatusResponse?>

    @POST("pgs/payment/v1/makePayment")
    suspend fun chargeUssd(@Body request: ChargeUssdRequest?): Response<ChargeUssdResponse?>

    @GET("pgs/payment/v1/getPaymentDetails/{clientId}/{trans}")
    suspend fun fetchUssdPaymentDetail(
        @Path("trans") reference: String,
        @Path("clientId") clientId: String
    ): Response<UssdPaymentDetailResponse?>

    @POST("pgs/clients/v1/publicKey")
    suspend fun insertPublicKey(@Body request: KeyRequest): Response<Nothing?>

    companion object {
        @Volatile
        private var INSTANCE: PaymentService? = null

        @JvmStatic
        fun getInstance(context: Context, config: ConfigProp): PaymentService {
            return INSTANCE ?: synchronized(this) {
                val logInterceptor = HttpLoggingInterceptor()
                if (LogUtils.showLog) {
                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    logInterceptor.level = HttpLoggingInterceptor.Level.NONE
                }

                val client = OkHttpClient.Builder()
                    .readTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .cache(createCache(context))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(AuthInterceptor(config))
                    .addInterceptor(logInterceptor)
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .setPrettyPrinting()
                    .create()

                val instance = Retrofit.Builder()
                    .baseUrl(config.baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create<PaymentService>()
                INSTANCE = instance
                instance
            }
        }

        private fun createCache(context: Context): Cache? {
            var cache: Cache? = null
            try {
                val cacheSize = (5 * 1024 * 1024).toLong()
                val cacheDir = File(context.cacheDir, "http-cache")
                cache = Cache(cacheDir, cacheSize)
            } catch (e: Exception) {
                LogUtils.e(e.message, e)
            }
            return cache
        }
    }
}
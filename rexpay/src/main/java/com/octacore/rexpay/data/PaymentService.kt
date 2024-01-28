@file:JvmSynthetic

package com.octacore.rexpay.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.octacore.rexpay.BuildConfig
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
    suspend fun chargeUssd(@Body request: ChargeUssdRequest): Response<ChargeUssdResponse?>

    @GET("pgs/payment/v1/getPaymentDetails/{TransRef}")
    suspend fun fetchUssdPaymentDetail(@Path("TransRef") reference: String): Response<UssdPaymentDetailResponse?>

    @POST("pgs/clients/v1/publicKey")
    suspend fun insertPublicKey(@Body request: KeyRequest): Response<Nothing?>

    companion object {
        @Volatile
        private var INSTANCE: PaymentService? = null

        @JvmStatic
        fun getInstance(context: Context): PaymentService {
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
                    .addInterceptor(AuthInterceptor())
                    .addInterceptor(logInterceptor)
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .setPrettyPrinting()
                    .create()

                val instance = Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create<PaymentService>()
                INSTANCE = instance
                instance
            }
        }

        @JvmStatic
        fun getInstance() = INSTANCE

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
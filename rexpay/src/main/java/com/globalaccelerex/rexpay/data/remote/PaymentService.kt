@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote

import com.google.gson.GsonBuilder
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankRequest
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankResponse
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdRequest
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdResponse
import com.globalaccelerex.rexpay.data.remote.models.EncryptedRequest
import com.globalaccelerex.rexpay.data.remote.models.EncryptedResponse
import com.globalaccelerex.rexpay.data.remote.models.KeyRequest
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationRequest
import com.globalaccelerex.rexpay.data.remote.models.PaymentCreationResponse
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusRequest
import com.globalaccelerex.rexpay.data.remote.models.TransactionStatusResponse
import com.globalaccelerex.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.globalaccelerex.rexpay.domain.models.Config
import com.globalaccelerex.rexpay.utils.AuthInterceptor
import com.globalaccelerex.rexpay.utils.LogUtils
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
import java.util.concurrent.TimeUnit

internal interface PaymentService {
    @POST("/api/pgs/payment/v2/createPayment")
    suspend fun createPayment(@Body request: PaymentCreationRequest): Response<PaymentCreationResponse?>

    @POST("/api/cps/v1/chargeCard")
    suspend fun chargeCard(@Body request: EncryptedRequest): Response<EncryptedResponse?>

    @POST("/api/cps/v1/authorizeTransaction")
    suspend fun authorizeTransaction(@Body request: EncryptedRequest): Response<EncryptedResponse?>

    @POST("/api/cps/v1/initiateBankTransfer")
    suspend fun chargeBank(@Body request: ChargeBankRequest): Response<ChargeBankResponse?>

    @POST("/api/cps/v1/getTransactionStatus")
    suspend fun fetchTransactionStatus(@Body request: TransactionStatusRequest): Response<TransactionStatusResponse?>

    @POST("/api/pgs/payment/v1/makePayment")
    suspend fun chargeUssd(@Body request: ChargeUssdRequest): Response<ChargeUssdResponse?>

    @GET("/api/pgs/payment/v1/getPaymentDetails/{trans}")
    suspend fun fetchUssdPaymentDetail(
        @Path("trans") reference: String,
    ): Response<UssdPaymentDetailResponse?>

    @POST("/api/pgs/clients/v1/publicKey")
    suspend fun insertPublicKey(@Body request: KeyRequest): Response<Void?>

    companion object {
        @Volatile
        private var INSTANCE: PaymentService? = null

        @JvmStatic
        fun getInstance(config: Config): PaymentService {
            return INSTANCE ?: synchronized(this) {
                val logInterceptor = HttpLoggingInterceptor()
                if (LogUtils.showLog) {
                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    logInterceptor.level = HttpLoggingInterceptor.Level.NONE
                }

                val client = OkHttpClient.Builder()
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(HostInterceptor(config))
                    .addInterceptor(AuthInterceptor(config))
                    .addInterceptor(logInterceptor)
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .disableHtmlEscaping()
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
    }
}
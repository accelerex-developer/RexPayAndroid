@file:JvmSynthetic

package com.globalaccelerex.rexpay.utils

import com.globalaccelerex.rexpay.domain.models.Config
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val config: Config) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val cred = Credentials.basic(config.username, config.password)
        request = request.newBuilder().header("Authorization", cred).build()
        return chain.proceed(request)
    }
}
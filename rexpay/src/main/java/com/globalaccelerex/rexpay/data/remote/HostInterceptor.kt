@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote

import com.globalaccelerex.rexpay.domain.models.Config
import com.globalaccelerex.rexpay.utils.LogUtils
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

internal class HostInterceptor(private val config: Config) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val host = if (config.isTest.not() && request.url.encodedPath.contains("cps")) {
            "https://cps.globalaccelerex.com"
        } else if (config.isTest.not() && request.url.encodedPath.contains("pgs")) {
            "https://pgs.globalaccelerex.com"
        } else "https://pgs-sandbox.globalaccelerex.com"
        LogUtils.i("HostInterceptor ==> Host: $host")

        val baseUrl = host.toHttpUrlOrNull()

        val newUrl = baseUrl?.host?.let {
            request.url.newBuilder()
                .scheme(baseUrl.scheme)
                .host(baseUrl.host)
                .port(baseUrl.port)
                .build()
        }
        request = newUrl?.let {
            request.newBuilder()
                .url(it)
                .build()
        } ?: request
        return chain.proceed(request)
    }
}
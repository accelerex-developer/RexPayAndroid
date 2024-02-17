@file:JvmSynthetic

package com.octacore.rexpay.data.remote

import com.octacore.rexpay.domain.models.Config
import com.octacore.rexpay.utils.LogUtils
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/02/2024
 **************************************************************************************************/
internal class HostInterceptor(private val config: Config) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

//        var host = config.baseUrl
//        var baseUrl = host.toHttpUrlOrNull()

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
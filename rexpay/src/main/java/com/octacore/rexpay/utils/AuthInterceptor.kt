@file:JvmSynthetic

package com.octacore.rexpay.utils

import com.octacore.rexpay.domain.models.ConfigProp
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class AuthInterceptor(private val config: ConfigProp) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val cred = Credentials.basic(config.username, config.passphrase)
        request = request.newBuilder().header("Authorization", cred).build()
        return chain.proceed(request)
    }
}
@file:JvmSynthetic

package com.octacore.rexpay.utils

import com.octacore.rexpay.BuildConfig
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
internal class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        LogUtils.i("Previous Request: $request")
        val cred = Credentials.basic(BuildConfig.API_USERNAME, BuildConfig.API_PASSWORD)
        request = request.newBuilder().header("Authorization", cred).build()
        LogUtils.i("Next Request: $request")
        return chain.proceed(request)
    }
}
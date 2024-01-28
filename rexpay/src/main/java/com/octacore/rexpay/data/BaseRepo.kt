@file:JvmSynthetic

package com.octacore.rexpay.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.octacore.rexpay.models.ErrorResponse
import com.octacore.rexpay.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal abstract class BaseRepo {

    protected suspend inline fun <reified T> processRequest(crossinline block: suspend () -> Response<T?>?): BaseResult<T?> {
        return withContext(Dispatchers.IO) {
            processRequestInternal(block) {
                return@withContext it
            }
        }
    }

    protected suspend inline fun <reified T, O> processRequestInternal(
        crossinline block: suspend () -> Response<T?>?,
        returnRes: (_: BaseResult<T?>) -> O
    ): O {
        try {
            val res = block()
            return if (res?.isSuccessful == true) {
                returnRes(BaseResult.Success(res.body()))
            } else {
                val errorBody = res?.errorBody()
                if (errorBody != null) {
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errRes = Gson().fromJson<ErrorResponse>(errorBody.charStream(), type)
                    returnRes(BaseResult.Error(errRes.responseMessage ?: "Something went wrong"))
                } else {
                    returnRes(BaseResult.Error(res?.message() ?: "Something went wrong"))
                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.message, e)
            return when (e) {
                is ConnectException -> returnRes(BaseResult.Error("Unable to connect, check your connection and try again"))
                is UnknownHostException -> returnRes(BaseResult.Error("Cannot connect to host. Try again later"))
                is SocketTimeoutException -> returnRes(BaseResult.Error("Connection timed out! Try again"))
                is HttpException -> {
                    try {
                        val msg = e.response()?.message()
                        returnRes(BaseResult.Error(msg ?: "Something went wrong"))
                    } catch (e: JSONException) {
                        LogUtils.e(e.message, e)
                        returnRes(BaseResult.Error("Error deserializing data"))
                    } catch (e: IOException) {
                        LogUtils.e(e.message, e)
                        returnRes(BaseResult.Error("Error reading data"))
                    } catch (e: SSLException) {
                        LogUtils.e(e.message, e)
                        returnRes(BaseResult.Error("Error with SSL connectivity"))
                    } catch (e: JsonSyntaxException) {
                        LogUtils.e(e.message, e)
                        returnRes(BaseResult.Error("Error deserializing data"))
                    } catch (e: IllegalStateException) {
                        LogUtils.e(e.message, e)
                        returnRes(BaseResult.Error("Something went wrong"))
                    }
                }
                is SSLException -> {
                    returnRes(BaseResult.Error("Error with SSL connectivity"))
                }
                else -> returnRes(BaseResult.Error("Something went wrong"))
            }
        }
    }
}
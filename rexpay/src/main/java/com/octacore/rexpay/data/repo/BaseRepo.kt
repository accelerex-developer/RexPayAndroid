@file:JvmSynthetic

package com.octacore.rexpay.data.repo

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.data.remote.models.ErrorResponse
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
    internal suspend inline fun <reified T> processRequest(crossinline block: suspend () -> Response<T?>?): BaseResult<T?> {
        return withContext(Dispatchers.IO) {
            LogUtils.i("Transaction started")
            processRequestInternal(block) {
                return@withContext it
            }
        }
    }

    internal suspend inline fun <reified T, O> processRequestInternal(
        crossinline block: suspend () -> Response<T?>?,
        returnRes: (_: BaseResult<T?>) -> O
    ): O {
        try {
            val res = block()
            return if (res?.isSuccessful == true) {
                LogUtils.i("Transaction Success: ${res.body()}")
                returnRes(BaseResult.Success(res.body()))
            } else {
                val errorBody = res?.errorBody()
                if (errorBody != null) {
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errRes = Gson().fromJson<ErrorResponse>(errorBody.charStream(), type)
                    LogUtils.e("Transaction Error: ${errRes.responseMessage ?: "Something went wrong"}")
                    returnRes(BaseResult.Error(errRes.responseMessage ?: "Something went wrong"))
                } else {
                    LogUtils.e("Transaction Error: ${res?.message() ?: "Something went wrong"}")
                    returnRes(BaseResult.Error(res?.message() ?: "Something went wrong"))
                }
            }
        } catch (e: Exception) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            return when (e) {
                is ConnectException -> returnRes(BaseResult.Error("Unable to connect, check your connection and try again"))
                is UnknownHostException -> returnRes(BaseResult.Error("Cannot connect to host. Try again later"))
                is SocketTimeoutException -> returnRes(BaseResult.Error("Connection timed out! Try again"))
                is HttpException -> {
                    try {
                        val msg = e.response()?.message()
                        returnRes(BaseResult.Error(msg ?: "Something went wrong"))
                    } catch (e: JSONException) {
                        LogUtils.e("Transaction Error: ${e.message}", e)
                        returnRes(BaseResult.Error("Error deserializing data"))
                    } catch (e: IOException) {
                        LogUtils.e("Transaction Error: ${e.message}", e)
                        returnRes(BaseResult.Error("Error reading data"))
                    } catch (e: SSLException) {
                        LogUtils.e("Transaction Error: ${e.message}", e)
                        returnRes(BaseResult.Error("Error with SSL connectivity"))
                    } catch (e: JsonSyntaxException) {
                        LogUtils.e("Transaction Error: ${e.message}", e)
                        returnRes(BaseResult.Error("Error deserializing data"))
                    } catch (e: IllegalStateException) {
                        LogUtils.e("Transaction Error: ${e.message}", e)
                        returnRes(BaseResult.Error("Something went wrong"))
                    }
                }

                is SSLException -> {
                    returnRes(BaseResult.Error("Error with SSL connectivity"))
                }

                else -> returnRes(BaseResult.Error("Something went wrong"))
            }
        } finally {
            LogUtils.i("Transaction ended")
        }
    }
}
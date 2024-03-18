@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.repo

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.globalaccelerex.rexpay.data.remote.models.ErrorResponse
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.BaseResult.Error
import com.globalaccelerex.rexpay.utils.LogUtils
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
                    val message = errRes.responseMessage ?: "Something went wrong"
                    LogUtils.e("Transaction Error: $message")
                    val errMsg = Error(message, errRes.responseCode, errRes.responseStatus)
                    returnRes(errMsg)
                } else {
                    val message = res?.message() ?: "Something went wrong"
                    LogUtils.e("Transaction Error: $message")
                    returnRes(Error(message = message))
                }
            }
        } catch (e: Exception) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            val message = when (e) {
                is ConnectException -> "Unable to connect, check your connection and try again"
                is UnknownHostException -> "Cannot connect to host. Try again later"
                is SocketTimeoutException -> "Connection timed out! Try again"
                is HttpException -> getHttpErrorResponse(e)
                is SSLException -> "Error with SSL connectivity"
                else -> "Something went wrong"
            }
            return returnRes(Error(message = message))
        } finally {
            LogUtils.i("Transaction ended")
        }
    }

    private fun getHttpErrorResponse(e: HttpException): String {
        return try {
            val msg = e.response()?.message()
            msg ?: "Something went wrong"
        } catch (e: JSONException) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            "Error deserializing data"
        } catch (e: IOException) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            "Error reading data"
        } catch (e: SSLException) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            "Error with SSL connectivity"
        } catch (e: JsonSyntaxException) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            "Error deserializing data"
        } catch (e: IllegalStateException) {
            LogUtils.e("Transaction Error: ${e.message}", e)
            "Something went wrong"
        }
    }
}
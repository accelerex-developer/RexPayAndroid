@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.remote.models

internal data class ErrorResponse(
    internal val responseMessage: String? = null,
    internal val responseCode: String? = null,
    internal val responseStatus: String? = null
)

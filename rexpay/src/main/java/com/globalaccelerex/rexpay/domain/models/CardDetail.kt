@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.models

internal data class CardDetail(
    internal val pan: String,
    internal val expiryDate: String,
    internal val cvv2: String,
    internal val pin: String,
)

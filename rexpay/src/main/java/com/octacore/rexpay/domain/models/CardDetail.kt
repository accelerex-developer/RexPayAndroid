@file:JvmSynthetic

package com.octacore.rexpay.domain.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 05/02/2024
 **************************************************************************************************/
internal data class CardDetail(
    internal val pan: String,
    internal val expiryDate: String,
    internal val cvv2: String,
    internal val pin: String,
)

package com.octacore.rexpay.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
@Parcelize
data class PayPayload(
    val reference: String,
    val amount: Long,
    val currency: String,
    val userId: String,
    val callbackUrl: String,
    val email: String,
    val customerName: String,
) : Parcelable
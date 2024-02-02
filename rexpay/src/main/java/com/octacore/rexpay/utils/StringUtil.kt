@file:JvmSynthetic

package com.octacore.rexpay.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/***************************************************************************************************
 *                          Copyright (C) 2023,  Octacore Tech.
 ***************************************************************************************************
 * Project         : POS
 * Author          : Gideon Chukwu
 * Date            : 12/12/2023
 **************************************************************************************************/
internal object StringUtil {

    @JvmStatic
    internal fun Number?.formatToNaira(currency: String = "NGN"): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
        formatter.currency = Currency.getInstance(currency)
        return try {
            formatter.format(this)
        } catch (e: IllegalArgumentException) {
            ""
        }
    }
}
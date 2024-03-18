@file:JvmSynthetic

package com.globalaccelerex.rexpay.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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
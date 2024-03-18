@file:JvmSynthetic

package com.globalaccelerex.rexpay.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.globalaccelerex.rexpay.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class CreditCardFormatter {
    internal var textFieldValue by mutableStateOf(TextFieldValue(""))

    internal var isInvalid by mutableStateOf<Boolean?>(null)

    internal val visualTransformation: VisualTransformation
        get() = VisualTransformation.None

    internal var suffixIcon by mutableStateOf<Int?>(null)

    internal fun formatCreditCard(textFieldValue: TextFieldValue) {
        val trimmedValue = textFieldValue.text.replace("\\s+".toRegex(), "")
        val formattedValue = StringBuilder()
        for (i in trimmedValue.indices) {
            if (i > 0 && i % 4 == 0) {
                formattedValue.append(' ')
            }
            formattedValue.append(trimmedValue[i])
        }

        suffixIcon = if (trimmedValue.length > 2) {
            if (trimmedValue.startsWith("506"))
                R.drawable.verve
            else if (trimmedValue.startsWith('4'))
                R.drawable.visa
            else R.drawable.mastercard
        } else null

        val limit = if (trimmedValue.startsWith("506")) 19 else 16

        val range = trimmedValue.length
        isInvalid = range < limit

        if (range in 0..limit) {
            this.textFieldValue = TextFieldValue(
                text = formattedValue.toString(),
                selection = TextRange(formattedValue.length)
            )
        }
    }
}

internal class ExpiryDateFormatter {
    internal var textFieldValue by mutableStateOf(TextFieldValue(""))

    internal var isInvalid by mutableStateOf<Boolean?>(null)

    internal val visualTransformation: VisualTransformation
        get() = VisualTransformation.None

    internal fun formatExpiryDate(textFieldValue: TextFieldValue) {
        var trimmedValue = textFieldValue.text.replace(Regex("[^0-9]"), "")

        if (trimmedValue.isNotEmpty() && trimmedValue.length < 3) {
            trimmedValue = if (trimmedValue.toInt() < 1 || trimmedValue.toInt() > 12) {
                trimmedValue.take(1)
            } else trimmedValue
        }

        val formattedValue = StringBuilder()
        for (i in trimmedValue.indices) {
            if (i > 0 && i % 2 == 0) {
                formattedValue.append('/')
            }
            formattedValue.append(trimmedValue[i])
            isInvalid = if (formattedValue.length < 5) {
                null
            } else {
                isValidDate(formattedValue.toString())
            }
        }
        val range = formattedValue.length
        if (range in 0..5) {
            this.textFieldValue = TextFieldValue(
                text = formattedValue.toString(),
                selection = TextRange(formattedValue.length)
            )
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        val formatter = SimpleDateFormat("MM/yy", Locale.getDefault())
        formatter.isLenient = false  // Disable leniency to enforce strict date parsing

        return try {
            val parsedDate = formatter.parse(dateStr)
            val calendar = Calendar.getInstance()
            if (parsedDate != null) {
                calendar.time = parsedDate
            }
            val currentDate = Calendar.getInstance()

            // Check if the parsed date is not later than the current date
            calendar.before(currentDate) || calendar == currentDate
        } catch (e: Exception) {
            true // Invalid date format or date out of range
        }
    }
}
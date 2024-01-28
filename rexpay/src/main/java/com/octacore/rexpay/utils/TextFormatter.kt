package com.octacore.rexpay.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 28/01/2024
 **************************************************************************************************/
class CreditCardFormatter {
    var textFieldValue by mutableStateOf(TextFieldValue(""))

    val visualTransformation: VisualTransformation
        get() = VisualTransformation.None

    fun formatCreditCard(textFieldValue: TextFieldValue) {
        val trimmedValue = textFieldValue.text.replace("\\s+".toRegex(), "")
        val formattedValue = StringBuilder()
        for (i in trimmedValue.indices) {
            if (i > 0 && i % 4 == 0) {
                formattedValue.append(' ')
            }
            formattedValue.append(trimmedValue[i])
        }
        val range = formattedValue.length
        if (range in 1..20) {
            this.textFieldValue = TextFieldValue(
                text = formattedValue.toString(),
                selection = TextRange(formattedValue.length)
            )
        }
    }
}

class ExpiryDateFormatter {
    var textFieldValue by mutableStateOf(TextFieldValue(""))

    val visualTransformation: VisualTransformation
        get() = VisualTransformation.None

    fun formatExpiryDate(textFieldValue: TextFieldValue) {
        val trimmedValue = textFieldValue.text.replace(Regex("[^A-Za-z0-9]") , "")

        val formattedValue = StringBuilder()
        for (i in trimmedValue.indices) {
            if (i > 0 && i % 2 == 0) {
                formattedValue.append('/')
            }
            formattedValue.append(trimmedValue[i])
        }
        val range = formattedValue.length
        if (range in 1..5) {
            this.textFieldValue = TextFieldValue(
                text = formattedValue.toString(),
                selection = TextRange(formattedValue.length)
            )
        }
    }
}
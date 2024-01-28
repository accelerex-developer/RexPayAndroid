@file:JvmSynthetic

package com.octacore.rexpay.ui.carddetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.octacore.rexpay.utils.CreditCardFormatter
import com.octacore.rexpay.utils.ExpiryDateFormatter

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 18/01/2024
 **************************************************************************************************/

internal class CardDetailViewModel : ViewModel() {
    var cardholder by mutableStateOf(CreditCardFormatter())
    var expiryDate by mutableStateOf(ExpiryDateFormatter())
    var cvv by mutableStateOf("")
    var pin by mutableStateOf("")
}
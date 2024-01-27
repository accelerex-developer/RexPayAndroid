@file:JvmSynthetic

package com.octacore.rexpay.models

import androidx.annotation.DrawableRes
import com.octacore.rexpay.R
import com.octacore.rexpay.ui.NavigationItem

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 18/01/2024
 **************************************************************************************************/

internal sealed class PaymentOptions(
    val id: Int,
    @DrawableRes val icon: Int,
    val title: String,
    val route: String = "",
) {
    data object CardOption :
        PaymentOptions(
            id = 0,
            icon = R.drawable.ic_credit_card,
            title = "Pay with Card",
            route = NavigationItem.CardDetail.route
        )

    data object UssdOption : PaymentOptions(
        id = 1,
        icon = R.drawable.ic_cellphone,
        title = "Pay with USSD",
    )

    data object BankOption : PaymentOptions(
        id = 2,
        icon = R.drawable.ic_bank,
        title = "Pay with Bank"
    )

    data object MobileMoneyOption : PaymentOptions(
        id = 3,
        icon = R.drawable.ic_mobile,
        title = "Pay with Mobile Money"
    )

    data object QRCodeOption : PaymentOptions(
        id = 4,
        icon = R.drawable.ic_qr_code,
        title = "Pay with QR"
    )

    companion object {
        val options = listOf(
            CardOption, UssdOption, BankOption, MobileMoneyOption, QRCodeOption
        )
    }
}
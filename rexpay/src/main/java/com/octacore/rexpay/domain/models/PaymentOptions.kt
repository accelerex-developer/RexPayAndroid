@file:JvmSynthetic

package com.octacore.rexpay.domain.models

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
    internal val id: Int,
    @DrawableRes internal val icon: Int,
    internal val title: String,
    internal val route: String = "",
    internal val active: Boolean,
    internal val shortName: String,
) {
    internal data object CardOption :
        PaymentOptions(
            id = 0,
            icon = R.drawable.ic_credit_card,
            title = "Pay with Card",
            route = NavigationItem.CardDetailScreen.route,
            active = true,
            shortName = "Card"
        )

    internal data object UssdOption : PaymentOptions(
        id = 1,
        icon = R.drawable.ic_cellphone,
        title = "Pay with USSD",
        active = true,
        shortName = "USSD",
        route = NavigationItem.USSDScreen.route
    )

    internal data object BankOption : PaymentOptions(
        id = 2,
        icon = R.drawable.ic_bank,
        title = "Pay with Bank",
        active = true,
        shortName = "Bank",
        route = NavigationItem.BankTransferScreen.route
    )

    internal data object MobileMoneyOption : PaymentOptions(
        id = 3,
        icon = R.drawable.ic_mobile,
        title = "Pay with Mobile Money",
        active = false,
        shortName = "Mobile Money"
    )

    internal data object QRCodeOption : PaymentOptions(
        id = 4,
        icon = R.drawable.ic_qr_code,
        title = "Pay with QR",
        active = false,
        shortName = "QRCode"
    )

    internal companion object {
        internal val options = listOf(
            CardOption, UssdOption, BankOption, MobileMoneyOption, QRCodeOption
        )
    }
}
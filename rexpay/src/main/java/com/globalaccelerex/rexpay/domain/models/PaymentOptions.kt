@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.models

import androidx.annotation.DrawableRes
import com.globalaccelerex.rexpay.R
import com.globalaccelerex.rexpay.ui.NavigationItem

internal sealed class PaymentOptions(
    @DrawableRes internal val icon: Int,
    internal val title: String,
    internal val route: String = "",
    internal val active: Boolean,
    internal val shortName: String,
) {
    internal data object CardOption :
        PaymentOptions(
            icon = R.drawable.ic_credit_card,
            title = "Pay with Card",
            route = NavigationItem.CardDetailScreen.route,
            active = true,
            shortName = "Card"
        )

    internal data object UssdOption : PaymentOptions(
        icon = R.drawable.ic_cellphone,
        title = "Pay with USSD",
        route = NavigationItem.USSDScreen.route,
        active = true,
        shortName = "USSD"
    )

    internal data object BankOption : PaymentOptions(
        icon = R.drawable.ic_bank,
        title = "Pay with Bank",
        route = NavigationItem.BankTransferScreen.route,
        active = true,
        shortName = "Bank"
    )

    internal data object MobileMoneyOption : PaymentOptions(
        icon = R.drawable.ic_mobile,
        title = "Pay with Mobile Money",
        active = false,
        shortName = "Mobile Money"
    )

    internal data object QRCodeOption : PaymentOptions(
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
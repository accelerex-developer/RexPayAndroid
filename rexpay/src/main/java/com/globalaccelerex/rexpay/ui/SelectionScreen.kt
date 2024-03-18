@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.domain.models.PaymentOptions
import com.globalaccelerex.rexpay.ui.theme.textBlack

@Composable
internal fun SelectionScreen(
    navController: NavHostController,
    cache: Cache = Cache.getInstance()
) {
    BaseBox(amount = cache.payload?.amount, userInfo = cache.payload?.userInfo) {
        Column {
            Text(
                text = "Please select your desired payment method to continue.",
                modifier = Modifier.padding(vertical = 16.dp),
                fontSize = 12.sp,
                color = textBlack.copy(alpha = 0.72F)
            )
            PaymentOptions.options.filter { it.active }.map { OptionItem(it, navController) }
        }
    }
}

@Composable
private fun OptionItem(
    option: PaymentOptions,
    navController: NavHostController
) {
    Box(modifier = Modifier
        .padding(vertical = 8.dp)
        .clickable {
            val navOption = NavOptions
                .Builder()
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(option.route, navOption)
        }) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(painter = painterResource(id = option.icon), contentDescription = null)
            Text(
                text = option.title,
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 16.dp),
                color = textBlack,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Forward arrow"
            )
        }
    }
}
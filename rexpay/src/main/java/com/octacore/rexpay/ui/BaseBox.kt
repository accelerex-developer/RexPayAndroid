@file:JvmSynthetic

package com.octacore.rexpay.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.octacore.rexpay.R
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.models.PaymentOptions
import com.octacore.rexpay.ui.theme.textBlack
import com.octacore.rexpay.ui.theme.textGray
import com.octacore.rexpay.utils.LogUtils
import com.octacore.rexpay.utils.StringUtil.formatToNaira

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
internal fun BaseBox(
    modifier: Modifier = Modifier,
    amount: Number? = 0,
    userInfo: String? = null,
    elevation: Dp = 0.5.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = Color.White,
        elevation = elevation
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .wrapContentSize()
        ) {
            Text(
                text = amount.formatToNaira(),
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                color = textBlack
            )
            Text(
                text = userInfo ?: "",
                fontSize = 12.sp,
                color = textGray
            )
            Divider(
                color = Color(0xFFEDEDED),
                thickness = 0.7.dp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .wrapContentWidth()
            )
            Box(modifier = Modifier.wrapContentSize(), content = content)
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Powered by: ", fontSize = 12.sp)
                Image(
                    painter = painterResource(id = R.drawable.accelerex),
                    contentDescription = "Accelerex Icon"
                )
            }
        }
    }
}

@Composable
internal fun BaseTopNav(navController: NavHostController) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        PaymentOptions.options.filter { it.active }
            .map { this.OptionItem(it, navController) }
    }
}

@Composable
private fun RowScope.OptionItem(
    option: PaymentOptions,
    navHostController: NavHostController,
) {
    val startId = navHostController.graph.startDestinationId
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .clickable {
                val options = NavOptions
                    .Builder()
                    .setPopUpTo(startId, inclusive = false)
                    .setLaunchSingleTop(true)
                    .build()
                navHostController.navigate(option.route, options)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painter = painterResource(id = option.icon), contentDescription = null)
            Text(
                text = option.shortName,
                modifier = Modifier
                    .padding(vertical = 4.dp),
                color = textBlack,
                fontSize = 10.sp
            )
        }
    }
}
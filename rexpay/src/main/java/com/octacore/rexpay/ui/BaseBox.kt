package com.octacore.rexpay.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.octacore.rexpay.R
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.models.PaymentOptions
import com.octacore.rexpay.ui.theme.RexpayTheme
import com.octacore.rexpay.ui.theme.textBlack
import com.octacore.rexpay.ui.theme.textGray
import com.octacore.rexpay.utils.StringUtil

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
fun BaseBox(
    payload: PayPayload?,
    modifier: Modifier = Modifier,
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
                text = StringUtil.formatToNaira(payload?.amount),
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                color = textBlack
            )
            Text(
                text = getTitle(payload),
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
fun BaseTopNav(navController: NavHostController) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        PaymentOptions.options.filter { it.active }
            .map { OptionItem(it, navController) }
    }
}

@Composable
private fun RowScope.OptionItem(option: PaymentOptions, navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .clickable {
                val options = NavOptions.Builder()
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

private fun getTitle(payload: PayPayload?): String {
    return when {
        payload?.userId.isNullOrEmpty().not() -> payload?.userId
        payload?.email.isNullOrEmpty().not() -> payload?.email
        payload?.customerName.isNullOrEmpty().not() -> payload?.customerName
        else -> ""
    } ?: ""
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun BasePreview() {
    RexpayTheme {
        BaseBox(null) { Box(modifier = Modifier) }
    }
}
package com.octacore.rexpay.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.octacore.rexpay.R
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.models.PaymentOptions
import com.octacore.rexpay.ui.theme.RexpayTheme
import com.octacore.rexpay.ui.theme.textBlack

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
fun SelectionScreen(navHostController: NavHostController, payload: PayPayload?) {
    BaseBox(payload) {
        Column {
            Text(
                text = "Please select your desired payment method to continue.",
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 12.sp,
                color = textBlack.copy(alpha = 0.72F)
            )
            PaymentOptions.options.map { OptionItem(it, navHostController) }
        }
    }
}

@Composable
private fun OptionItem(option: PaymentOptions, navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { navHostController.navigate(option.route) }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(painter = painterResource(id = option.icon), contentDescription = null)
            Text(
                text = option.title, modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 16.dp),
                color = textBlack,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = "Forward arrow"
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SelectionPreview() {
    RexpayTheme {
        SelectionScreen(navHostController = rememberNavController(), null)
    }
}
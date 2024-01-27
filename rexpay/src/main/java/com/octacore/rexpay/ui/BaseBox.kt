package com.octacore.rexpay.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.octacore.rexpay.R
import com.octacore.rexpay.models.PayPayload
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
fun BaseBox(payload: PayPayload?, content: @Composable BoxScope.() -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                text = payload?.customerName ?: payload?.email ?: payload?.userId ?: "",
                fontSize = 12.sp,
                color = textGray
            )
            Divider(
                color = Color(0xFFEDEDED),
                thickness = 0.7.dp,
                modifier = Modifier
                    .padding(vertical = 16.dp)
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
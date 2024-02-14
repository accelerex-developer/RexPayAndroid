@file:JvmSynthetic

package com.octacore.rexpay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.octacore.rexpay.R
import com.octacore.rexpay.ui.theme.PoppinsFamily
import com.octacore.rexpay.ui.theme.Red
import com.octacore.rexpay.utils.StringUtil.formatToNaira

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 02/02/2024
 **************************************************************************************************/

@Composable
internal fun SuccessScreen(
    navController: NavHostController,
    amount: Long?,
) {
    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(4.dp),
            backgroundColor = Color.White,
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec
                        .RawRes(R.raw.success_anim)
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true,
                    restartOnPlay = false
                )
                LottieAnimation(
                    composition,
                    { progress },
                    modifier = Modifier.size(120.dp)
                )
                val annotatedString = buildAnnotatedString {
                    append("You have made a payment of ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${amount.formatToNaira()}.")
                    }
                    append("\nWe have sent a receipt to your mail")
                }
                Text(
                    text = "Payment Successful",
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(top = 32.dp)
                )
                Text(
                    text = annotatedString,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 32.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Red)
                ) {
                    Text(text = "Go to Dashboard", fontSize = 12.sp, fontFamily = PoppinsFamily)
                }
            }
        }
    }
}
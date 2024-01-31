@file:JvmSynthetic

package com.octacore.rexpay.ui.banktransfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav
import com.octacore.rexpay.ui.NavigationItem
import com.octacore.rexpay.ui.theme.Red

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/

@Composable
internal fun BankTransferScreen(
    navController: NavHostController,
    payload: PayPayload?,
    vm: BankTransferViewModel = viewModel(),
) {
    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController)
        BaseBox(payment = null, elevation = 2.dp) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = {
                    val options = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    navController.navigate(NavigationItem.OTP.route, options)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Red,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
                content = {
                    Text(text = "Pay")
                }
            )
        }
    }
}
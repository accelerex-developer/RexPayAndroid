@file:JvmSynthetic

package com.octacore.rexpay.ui.banktransfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav
import com.octacore.rexpay.ui.NavigationItem
import com.octacore.rexpay.ui.theme.PoppinsFamily
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
    vm: BankTransferViewModel = viewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    if (uiState.errorMsg != null) {

    }

    if (uiState.account != null) {
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(
            NavigationItem.BankTransferDetail.route + "/${uiState.payment?.reference}",
            options
        )
        vm.reset()
    }

    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController, reference = uiState.payment?.reference)
        BaseBox(payment = uiState.payment, elevation = 2.dp) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Kindly click the button to get account details",
                    fontSize = 14.sp,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    onClick = { vm.initiate() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Red,
                        contentColor = Color.White
                    ),
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    content = {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Pay")
                        }
                    }
                )
            }
        }
    }
}
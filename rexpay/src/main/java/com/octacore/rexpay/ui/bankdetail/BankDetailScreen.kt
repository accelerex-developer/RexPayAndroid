@file:JvmSynthetic

package com.octacore.rexpay.ui.bankdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav
import com.octacore.rexpay.ui.theme.PurpleGrey40
import com.octacore.rexpay.ui.theme.Red

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
@Composable
internal fun BankDetailScreen(
    navController: NavHostController,
    vm: BankDetailViewModel = viewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val account = uiState.account
    val payment = account?.payment
    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController, reference = payment?.reference)
        BaseBox(payment = payment, elevation = 2.dp) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Kindly proceed to your banking app mobile/internet to complete your bank transfer.",
                    fontSize = 11.sp
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "Please note the account number expires in 30 minutes.",
                    fontSize = 11.sp
                )
                if (account != null) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .fillMaxWidth()
                            .background(color = PurpleGrey40.copy(alpha = 0.2F))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Bank:", fontSize = 14.sp)
                            Text(text = account.bankName, fontWeight = FontWeight.W600)
                            Text(
                                text = "Account Name:",
                                modifier = Modifier.padding(top = 8.dp),
                                fontSize = 14.sp
                            )
                            Text(text = account.accountName, fontWeight = FontWeight.W600)
                            Text(
                                text = "Account Number:",
                                modifier = Modifier.padding(top = 8.dp),
                                fontSize = 14.sp
                            )
                            Text(text = account.accountNumber, fontWeight = FontWeight.W600)
                        }
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    onClick = { vm.confirmTransaction() },
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
                            Text(text = "I have completed the transfer")
                        }
                    }
                )
            }
        }
    }
}
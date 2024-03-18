@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.bankdetail

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.globalaccelerex.rexpay.R
import com.globalaccelerex.rexpay.components.PaymentManager
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.ui.BaseBox
import com.globalaccelerex.rexpay.ui.BaseTopNav
import com.globalaccelerex.rexpay.ui.CustomDialog
import com.globalaccelerex.rexpay.ui.NavigationItem
import com.globalaccelerex.rexpay.ui.theme.PurpleGrey40
import com.globalaccelerex.rexpay.ui.theme.Red

@Composable
internal fun BankDetailScreen(
    navController: NavHostController,
    manager: PaymentManager = PaymentManager.getInstance(),
    vm: BankDetailViewModel = viewModel(),
    context: Context = LocalContext.current,
) {
    val cache by lazy { Cache.getInstance() }
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    if (uiState.response != null) {
        if (uiState.response?.responseCode == "02") {
            PendingDialog(
                message = uiState.response?.responseDescription,
                close = { vm.reset() },
            )
        } else if (uiState.response?.responseCode == "00") {
            val startId = navController.graph.startDestinationId
            val option = NavOptions.Builder()
                .setPopUpTo(startId, inclusive = false)
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(NavigationItem.SuccessScreen.route, option)
            vm.reset()
        }
    }
    if (uiState.errorMsg != null) {
        ErrorDialog(
            onClose = {
                val err = PayResult.Error(uiState.errorMsg)
                manager.onResponse(context, err)
                vm.reset()
            },
            onContinue = { /*TODO*/ },
            message = uiState.errorMsg?.message ?: "Something went wrong"
        )
    }

    val account = cache.bankAccount
    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController)
        BaseBox(
            amount = cache.payload?.amount,
            userInfo = cache.payload?.userInfo,
            elevation = 2.dp
        ) {
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
                            .background(color = PurpleGrey40.copy(alpha = 0.1F))
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
                    onClick = { vm.confirmTransaction(account?.reference) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Red,
                        contentColor = Color.White
                    ),
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(vertical = 12.dp),
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

@Composable
private fun PendingDialog(
    message: String?,
    close: () -> Unit,
) {
    CustomDialog(
        negativeText = "Close",
        horizontalAlignment = Alignment.CenterHorizontally,
        onDismissRequest = {},
        onPositiveClicked = {},
        onNegativeClicked = close
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec
                .RawRes(R.raw.pending_anim)
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
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = message ?: "",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun ErrorDialog(
    onClose: () -> Unit,
    onContinue: () -> Unit,
    message: String,
) {
    CustomDialog(
        positiveText = "Retry",
        negativeText = "Cancel",
        horizontalAlignment = Alignment.CenterHorizontally,
        onDismissRequest = { },
        onPositiveClicked = onContinue,
        onNegativeClicked = onClose
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec
                .RawRes(R.raw.error_anim)
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
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = message,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
        )
    }
}
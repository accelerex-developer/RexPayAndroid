@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.banktransfer

import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.globalaccelerex.rexpay.ui.theme.Red

@Composable
internal fun BankTransferScreen(
    navController: NavHostController,
    vm: BankTransferViewModel = viewModel(),
    manager: PaymentManager = PaymentManager.getInstance(),
    context: Context = LocalContext.current
) {
    val cache by lazy { Cache.getInstance() }
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    if (uiState.errorMsg != null) {
        ErrorDialog(
            onClose = {
                val err = PayResult.Error(uiState.errorMsg)
                manager.onResponse(context, err)
                vm.reset()
            },
            onContinue = { vm.reset() },
            message = uiState.errorMsg?.message ?: "Something went wrong"
        )
    }

    if (uiState.account != null) {
        val startId = navController.graph.startDestinationId
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(startId, inclusive = false)
            .build()
        navController.navigate(
            NavigationItem.BankDetailScreen.route,
            options
        )
        vm.reset()
    }

    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController)
        BaseBox(
            amount = cache.payload?.amount,
            userInfo = cache.payload?.userInfo,
            elevation = 2.dp
        ) {
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
                    contentPadding = PaddingValues(vertical = 12.dp),
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
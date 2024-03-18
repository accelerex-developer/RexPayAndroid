@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.otp

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.data.remote.models.AuthorizeCardResponse
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.ui.BaseBox
import com.globalaccelerex.rexpay.ui.BaseTopNav
import com.globalaccelerex.rexpay.ui.CustomDialog
import com.globalaccelerex.rexpay.ui.NavigationItem
import com.globalaccelerex.rexpay.ui.theme.Red
import com.globalaccelerex.rexpay.ui.theme.lineGray
import com.globalaccelerex.rexpay.ui.theme.textBlack

@Composable
internal fun OtpScreen(
    navController: NavHostController,
    vm: OtpViewModel = viewModel(),
    context: Context = LocalContext.current,
    cache: Cache = Cache.getInstance(),
    manager: PaymentManager = PaymentManager.getInstance()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    fun goBack(res: AuthorizeCardResponse? = null) {
        val start = navController.graph.startDestinationId
        navController.popBackStack(start, true)
        val error =
            res?.let { BaseResult.Error(message = "An error occurred", code = it.responseCode) }
                ?: uiState.errorMsg
        val result = PayResult.Error(error)
        manager.onResponse(context, result)
    }

    if (uiState.response != null) {
        if (uiState.response?.responseCode == "00") {
            val start = navController.graph.startDestinationId
            val options = NavOptions.Builder()
                .setPopUpTo(start, true)
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(NavigationItem.SuccessScreen.route, options)
        } else {
            ErrorDialog(
                onClose = { goBack(uiState.response) },
                onContinue = { vm.reset() },
                message = "An error occurred"
            )
        }
    }

    if (uiState.errorMsg != null) {
        ErrorDialog(
            onClose = { goBack() },
            onContinue = { vm.reset() },
            message = uiState.errorMsg?.message ?: "An error occurred"
        )
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
                    text = "We sent an OTP to your device. Please, enter the OTP below to confirm transaction.",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = textBlack.copy(alpha = 0.72F)
                )
                OutlinedTextField(
                    value = vm.pin,
                    onValueChange = { if (it.length in 0..6) vm.pin = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 12.sp
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = lineGray.copy(alpha = 0.64F),
                        unfocusedBorderColor = lineGray.copy(alpha = 0.64F)
                    )
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    enabled = !uiState.isLoading && vm.pin.length > 3 && vm.pin.length < 7,
                    onClick = { vm.processTransaction() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Red,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    content = {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Submit")
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
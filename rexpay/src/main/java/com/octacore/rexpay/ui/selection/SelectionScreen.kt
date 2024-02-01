@file:JvmSynthetic

package com.octacore.rexpay.ui.selection

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.octacore.rexpay.R
import com.octacore.rexpay.RexPay
import com.octacore.rexpay.components.PaymentManager
import com.octacore.rexpay.domain.models.PayResult
import com.octacore.rexpay.domain.models.PaymentOptions
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.CustomDialog
import com.octacore.rexpay.ui.theme.RexPayTheme
import com.octacore.rexpay.ui.theme.textBlack
import com.octacore.rexpay.utils.getActivity

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
internal fun SelectionScreen(
    manager: PaymentManager = PaymentManager.getInstance(),
    navController: NavHostController,
    viewModel: SelectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoading && uiState.response == null && uiState.errorMsg == null) {
        val res = navController.navigateUp()
        if (res.not()) {
            val activity = LocalContext.current.getActivity()
            activity?.finishAfterTransition()
        }
    } else if (uiState.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) { CircularProgressIndicator() }
    } else if (uiState.errorMsg != null) {
        CustomDialog(
            positiveText = "Retry",
            negativeText = "Cancel",
            horizontalAlignment = Alignment.CenterHorizontally,
            onDismissRequest = { },
            onPositiveClicked = { viewModel.initiateTransaction() },
            onNegativeClicked = {
                val err = PayResult.Error(uiState.errorMsg)
                manager.onResponse(err)
                viewModel.dismissError()
            }) {
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
                progress,
                modifier = Modifier.size(120.dp)
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = uiState.errorMsg?.message!!,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
            )
        }
    } else {
        BaseBox(uiState.response) {
            Column {
                Text(
                    text = "Please select your desired payment method to continue.",
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontSize = 12.sp,
                    color = textBlack.copy(alpha = 0.72F)
                )
                PaymentOptions.options.filter { it.active }
                    .map { OptionItem(it, uiState.response?.reference, navController) }
            }
        }
    }
}

@Composable
private fun OptionItem(
    option: PaymentOptions,
    reference: String?,
    navController: NavHostController
) {
    Box(modifier = Modifier
        .padding(vertical = 8.dp)
        .clickable {
            val navOption = NavOptions
                .Builder()
                .setLaunchSingleTop(true)
                .build()
            navController.navigate(option.route + "/$reference", navOption)
        }) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(painter = painterResource(id = option.icon), contentDescription = null)
            Text(
                text = option.title,
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 16.dp),
                color = textBlack,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Forward arrow"
            )
        }
    }
}
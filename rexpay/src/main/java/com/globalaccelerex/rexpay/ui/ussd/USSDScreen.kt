@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.ussd

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.globalaccelerex.rexpay.R
import com.globalaccelerex.rexpay.components.PaymentManager
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.cache.Cache
import com.globalaccelerex.rexpay.data.remote.models.ChargeUssdResponse
import com.globalaccelerex.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.domain.models.USSDBank
import com.globalaccelerex.rexpay.ui.BaseBox
import com.globalaccelerex.rexpay.ui.BaseTopNav
import com.globalaccelerex.rexpay.ui.CustomDialog
import com.globalaccelerex.rexpay.ui.theme.PoppinsFamily
import com.globalaccelerex.rexpay.ui.theme.PurpleGrey40
import com.globalaccelerex.rexpay.ui.theme.Red
import com.globalaccelerex.rexpay.ui.theme.lineGray
import com.globalaccelerex.rexpay.ui.theme.textBlack
import com.globalaccelerex.rexpay.ui.theme.textGray
import com.globalaccelerex.rexpay.utils.listFromAsset

@Composable
internal fun USSDScreen(
    navController: NavHostController,
    vm: USSDViewModel = viewModel(),
    context: Context = LocalContext.current,
    manager: PaymentManager = PaymentManager.getInstance()
) {
    val cache by lazy { Cache.getInstance() }
    val chargeState by vm.chargeUSSDState.collectAsStateWithLifecycle()
    val verifyState by vm.verifyUSSDState.collectAsStateWithLifecycle()
    var isExpanded by remember { mutableStateOf(false) }

    fun goBack(res: ChargeUssdResponse? = null, verify: UssdPaymentDetailResponse? = null) {
        val start = navController.graph.startDestinationId
        navController.popBackStack(start, true)
        val error = res?.let {
            BaseResult.Error(
                message = it.providerResponse ?: "Transaction could not be completed",
                code = "01"
            )
        } ?: verify?.let {
            BaseResult.Error(
                message = it.providerResponse ?: "Transaction could not be completed",
                code = "01"
            )
        } ?: chargeState.errorMsg ?: verifyState.errorMsg
        val result = PayResult.Error(error)
        manager.onResponse(context, result)
    }

    if (chargeState.errorMsg != null) {
        ErrorDialog(
            showCancel = false,
            positiveText = "Okay",
            onClose = { goBack() },
            onContinue = { vm.reset(1) },
            message = chargeState.errorMsg?.message ?: "Something went wrong"
        )
    }

    if (verifyState.errorMsg != null) {
        ErrorDialog(
            onClose = { goBack() },
            onContinue = { vm.reset(2) },
            message = verifyState.errorMsg?.message ?: "Something went wrong"
        )
    }

    val verifyRes = verifyState.response
    if (verifyRes != null) {
        if (verifyRes.status == "ONGOING") {
            PendingDialog(
                message = verifyRes.statusMessage,
                close = { vm.reset(2) },
            )
        }
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
                    text = "Please, choose a bank to continue with payment",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = textBlack.copy(alpha = 0.72F)
                )
                BankSelector(
                    isExpanded = isExpanded,
                    selectedBank = vm.selectedBank.value,
                    isLoading = chargeState.isLoading,
                    onExpandedChanged = {
                        if (!verifyState.isLoading) {
                            isExpanded = it
                        }
                    },
                    onDismiss = { isExpanded = false },
                    onSelected = {
                        vm.onBankSelected(it)
                        isExpanded = false
                    }
                )
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .background(color = PurpleGrey40.copy(alpha = 0.1F)),
                    contentAlignment = Alignment.Center
                ) {
                    if (chargeState.response == null) {
                        Text(
                            text = "Your USSD Payment code will appear here",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Dial the below code to complete payment", fontSize = 12.sp)
                            Text(
                                text = chargeState.response?.providerResponse ?: "",
                                fontWeight = FontWeight.W600,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            TextButton(
                                onClick = { vm.checkTransactionStatus(chargeState.response?.reference) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Red)
                            ) {
                                if (verifyState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Check Transaction Status",
                                        fontSize = 12.sp,
                                        fontFamily = PoppinsFamily,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BankSelector(
    isExpanded: Boolean,
    selectedBank: USSDBank?,
    onExpandedChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSelected: (USSDBank?) -> Unit,
    isLoading: Boolean,
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        modifier = Modifier.fillMaxWidth(),
        onExpandedChange = onExpandedChanged
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedBank?.display ?: "",
            onValueChange = {},
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 1.dp
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                }
            },
            placeholder = {
                Text(
                    text = "Select Bank",
                    fontSize = 12.sp,
                    color = textGray.copy(alpha = 0.72F)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = lineGray.copy(alpha = 0.32F),
                unfocusedBorderColor = lineGray.copy(alpha = 0.32F)
            ),
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                onClick = { onSelected.invoke(null) },
                content = { Text(text = "Select Bank...") },
            )
            val banks = LocalContext.current.listFromAsset<USSDBank>("banks.json")
            for (bank in banks) {
                DropdownMenuItem(
                    onClick = { onSelected.invoke(bank) },
                    content = { Text(text = bank.display) },
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
    showRetry: Boolean = true,
    showCancel: Boolean = true,
    positiveText: String = "Retry",
    negativeText: String = "Cancel"
) {
    CustomDialog(
        positiveText = if (showRetry) positiveText else null,
        negativeText = if (showCancel) negativeText else null,
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
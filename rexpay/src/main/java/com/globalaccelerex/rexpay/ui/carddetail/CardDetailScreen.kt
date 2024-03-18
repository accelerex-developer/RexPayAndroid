@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.carddetail

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
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
import com.globalaccelerex.rexpay.data.remote.models.ChargeCardResponse
import com.globalaccelerex.rexpay.domain.models.PayResult
import com.globalaccelerex.rexpay.ui.BaseBox
import com.globalaccelerex.rexpay.ui.BaseTopNav
import com.globalaccelerex.rexpay.ui.CustomDialog
import com.globalaccelerex.rexpay.ui.NavigationItem
import com.globalaccelerex.rexpay.ui.theme.Red
import com.globalaccelerex.rexpay.ui.theme.lineGray
import com.globalaccelerex.rexpay.ui.theme.textBlack
import com.globalaccelerex.rexpay.ui.theme.textGray

@Composable
internal fun CardDetailScreen(
    navController: NavHostController,
    vm: CardDetailViewModel = viewModel(),
    context: Context = LocalContext.current,
    cache: Cache = Cache.getInstance(),
    manager: PaymentManager = PaymentManager.getInstance()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    fun goBack(res: ChargeCardResponse? = null) {
        val start = navController.graph.startDestinationId
        navController.popBackStack(start, true)
        val error = res?.let {
            BaseResult.Error(
                message = it.responseDescription ?: "Transaction could not be completed",
                code = it.responseCode
            )
        } ?: uiState.errorMsg
        val result = PayResult.Error(error)
        manager.onResponse(context, result)
    }

    val res = uiState.response
    if (res != null) {
        when (res.responseCode) {
            "T0" -> {
                val options = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
                navController.navigate(NavigationItem.OTPScreen.route, options)
            }

            else -> {
                ErrorDialog(
                    onClose = { goBack(res) },
                    onContinue = { vm.reset() },
                    message = res.responseDescription ?: "Transaction could not be completed"
                )
            }
        }
    }

    if (uiState.errorMsg != null) {
        ErrorDialog(
            onClose = { goBack() },
            onContinue = { vm.reset() },
            message = uiState.errorMsg?.message ?: "Something went wrong"
        )
    }

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        BaseTopNav(navController = navController)
        BaseBox(
            amount = cache.payload?.amount,
            userInfo = cache.payload?.userInfo,
            elevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Please, enter your card details to make payment.",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = textBlack.copy(alpha = 0.72F)
                )
                TextOutlineForm(
                    value = vm.cardholder.textFieldValue,
                    label = "Card Number",
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = vm.cardholder.visualTransformation,
                    trailingIcon = vm.cardholder.suffixIcon,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                ) {
                    vm.cardholder.formatCreditCard(it)
                    vm.checkValues()
                    if (vm.cardholder.isInvalid == false) {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    TextOutlineForm(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        value = vm.expiryDate.textFieldValue,
                        isError = vm.expiryDate.isInvalid ?: false,
                        visualTransformation = vm.expiryDate.visualTransformation,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = "Expiry Date"
                    ) {
                        vm.expiryDate.formatExpiryDate(it)
                        vm.checkValues()
                        if (vm.expiryDate.isInvalid == false) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                    }
                    TextOutlineForm(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        value = vm.cvv,
                        label = "CVV",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                    ) {
                        if (it.length in 0..3) vm.cvv = it
                        vm.checkValues()
                        if (it.length == 3) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                    }
                }
                TextOutlineForm(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    value = vm.pin,
                    label = "PIN",
                    labelAlign = Alignment.Center,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        letterSpacing = 12.sp
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                ) {
                    if (it.length in 0..6) vm.pin = it
                    vm.checkValues()
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    enabled = !uiState.isLoading && vm.enableButton,
                    onClick = {
                        keyboardManager?.hide()
                        vm.initiateCardPayment()
                    },
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
                            Text(text = "Proceed")
                        }
                    }
                )
            }
        }
    }
}

@Composable
internal fun TextOutlineForm(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    labelAlign: Alignment = Alignment.CenterStart,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    textStyle: TextStyle = LocalTextStyle.current,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = textStyle,
        placeholder = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    modifier = Modifier.align(labelAlign),
                    color = textGray.copy(alpha = 0.72F)
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = lineGray.copy(alpha = 0.64F),
            unfocusedBorderColor = lineGray.copy(alpha = 0.64F)
        )
    )
}

@Composable
internal fun TextOutlineForm(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String,
    isError: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @DrawableRes trailingIcon: Int? = null,
    onChange: (TextFieldValue) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = modifier,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        placeholder = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = textGray.copy(alpha = 0.72F)
            )
        },
        trailingIcon = trailingIcon?.let {
            {
                Image(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = lineGray.copy(alpha = 0.64F),
            unfocusedBorderColor = lineGray.copy(alpha = 0.64F)
        )
    )
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
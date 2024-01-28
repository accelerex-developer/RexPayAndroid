package com.octacore.rexpay.ui.carddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav
import com.octacore.rexpay.ui.theme.Red
import com.octacore.rexpay.ui.theme.lineGray
import com.octacore.rexpay.ui.theme.textBlack
import com.octacore.rexpay.ui.theme.textGray

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 18/01/2024
 **************************************************************************************************/

@Composable
internal fun CardDetailScreen(
    navController: NavHostController,
    payload: PayPayload?,
    viewModel: CardDetailViewModel = viewModel(),
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        BaseTopNav(navController = navController)
        BaseBox(payload = payload, elevation = 2.dp) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Please, enter your card details to make payment.",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 12.sp,
                    color = textBlack.copy(alpha = 0.72F)
                )
                TextOutlineForm(
                    value = viewModel.cardholder.textFieldValue,
                    label = "Card Number",
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = viewModel.cardholder.visualTransformation,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                ) { viewModel.cardholder.formatCreditCard(it) }
                Row(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    TextOutlineForm(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        value = viewModel.expiryDate.textFieldValue,
                        visualTransformation = viewModel.expiryDate.visualTransformation,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = "Expiry Date"
                    ) { viewModel.expiryDate.formatExpiryDate(it) }
                    TextOutlineForm(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        value = viewModel.cvv,
                        label = "CVV",
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    ) { if (it.length in 0..3) viewModel.cvv = it }
                }
                TextOutlineForm(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    value = viewModel.pin,
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
                ) { if (it.length in 0..6) viewModel.pin = it }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    onClick = {},
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
}

@Composable
fun TextOutlineForm(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    labelAlign: Alignment = Alignment.CenterStart,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
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
            focusedBorderColor = lineGray.copy(alpha = 0.32F),
            unfocusedBorderColor = lineGray.copy(alpha = 0.32F)
        )
    )
}

@Composable
fun TextOutlineForm(
    value: TextFieldValue,
    label: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        placeholder = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = textGray.copy(alpha = 0.72F)
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = lineGray.copy(alpha = 0.32F),
            unfocusedBorderColor = lineGray.copy(alpha = 0.32F)
        )
    )
}
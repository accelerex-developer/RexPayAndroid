package com.octacore.rexpay.ui.carddetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.BaseBox
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
    BaseBox(payload = payload) {
        Column {
            Text(
                text = "Please, enter your card details to make payment.",
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 12.sp,
                color = textBlack.copy(alpha = 0.72F)
            )
            TextOutlineForm(
                value = viewModel.cardholder,
                label = "Card Number",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            ) { viewModel.cardholder = it }
            Row(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                TextOutlineForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .weight(1f),
                    value = viewModel.expiryDate,
                    label = "Expiry Date"
                ) { viewModel.expiryDate = it }
                TextOutlineForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .weight(1f),
                    value = viewModel.cvv,
                    label = "CVV"
                ) { viewModel.cvv = it }
            }
        }
    }
}

@Composable
fun TextOutlineForm(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        placeholder = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = textGray.copy(alpha = 0.72F)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = lineGray.copy(alpha = 0.32F),
            unfocusedIndicatorColor = lineGray.copy(alpha = 0.32F),
        )
    )
}
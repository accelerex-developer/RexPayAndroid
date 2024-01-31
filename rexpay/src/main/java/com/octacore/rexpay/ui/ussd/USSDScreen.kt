@file:JvmSynthetic

package com.octacore.rexpay.ui.ussd

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.models.USSDBank
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav
import com.octacore.rexpay.ui.theme.lineGray
import com.octacore.rexpay.ui.theme.textBlack
import com.octacore.rexpay.ui.theme.textGray
import com.octacore.rexpay.utils.listFromAsset

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/

@Composable
internal fun USSDScreen(
    navController: NavHostController,
    vm: USSDViewModel = viewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var isExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController, reference = uiState.payment?.reference)
        BaseBox(payment = uiState.payment, elevation = 2.dp) {
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
                    onExpandedChanged = { isExpanded = it },
                    onDismiss = { isExpanded = false },
                    onSelected = {
                        vm.onBankSelected(it)
                        isExpanded = false
                    }
                )
                USSDCodeDisplay(
                    isLoading = uiState.isLoading,
                    response = uiState.payment
                )
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
    onSelected: (USSDBank?) -> Unit
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
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
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
private fun USSDCodeDisplay(
    isLoading: Boolean = false,
    response: Payment? = null
) {
    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) { CircularProgressIndicator() }
    } else if (response != null) {

    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 48.dp)
        ) {
            Text(
                text = "Your USSD Payment code will appear here",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
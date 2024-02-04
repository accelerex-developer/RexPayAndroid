@file:JvmSynthetic

package com.octacore.rexpay.ui.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.octacore.rexpay.ui.BaseBox
import com.octacore.rexpay.ui.BaseTopNav

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 29/01/2024
 **************************************************************************************************/

@Composable
internal fun OtpScreen(
    navController: NavHostController,
    vm: OtpViewModel = viewModel(),
) {
    Column(verticalArrangement = Arrangement.Center) {
        BaseTopNav(navController = navController)
        BaseBox(elevation = 2.dp) {

        }
    }
}
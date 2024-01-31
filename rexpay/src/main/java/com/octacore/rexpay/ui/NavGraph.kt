@file:JvmSynthetic

package com.octacore.rexpay.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.octacore.rexpay.DI
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.ui.banktransfer.BankTransferScreen
import com.octacore.rexpay.ui.carddetail.CardDetailScreen
import com.octacore.rexpay.ui.carddetail.CardDetailViewModel
import com.octacore.rexpay.ui.otp.OtpScreen
import com.octacore.rexpay.ui.selection.SelectionScreen
import com.octacore.rexpay.ui.selection.SelectionViewModel
import com.octacore.rexpay.ui.ussd.USSDScreen
import com.octacore.rexpay.ui.ussd.USSDViewModel

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
internal fun AppNavGraph(
    activity: Activity,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Selection.route + "/{reference}",
    payload: PayPayload?,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavigationItem.Selection.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
                defaultValue = payload?.reference
            }
        )) {
            val factory = SelectionViewModel.provideFactory(DI.basePaymentRepo)
            val viewModel = viewModel<SelectionViewModel>(factory = factory)
            SelectionScreen(
                activity = activity,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(NavigationItem.CardDetail.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory = CardDetailViewModel.provideFactory(DI.cardRepo)
            val viewModel: CardDetailViewModel = viewModel(factory = factory)
            CardDetailScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.OTP.route) {
            OtpScreen(navController, payload)
        }
        composable(NavigationItem.BankTransfer.route) {
            BankTransferScreen(navController, payload)
        }
        composable(NavigationItem.USSD.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory = USSDViewModel.provideFactory(DI.ussdRepo)
            val viewModel: USSDViewModel = viewModel(factory = factory)
            USSDScreen(navController = navController, vm = viewModel)
        }
    }
}

internal enum class Screen {
    SELECTION,
    CARD_DETAIL,
    OTP,
    BANK_TRANSFER,
    USSD,
}

internal sealed class NavigationItem(val route: String) {
    data object Selection : NavigationItem(Screen.SELECTION.name)
    data object CardDetail : NavigationItem(Screen.CARD_DETAIL.name)
    data object OTP : NavigationItem(Screen.OTP.name)
    data object BankTransfer : NavigationItem(Screen.BANK_TRANSFER.name)
    data object USSD : NavigationItem(Screen.USSD.name)
}
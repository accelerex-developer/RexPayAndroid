@file:JvmSynthetic

package com.octacore.rexpay.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.octacore.rexpay.RexPayApp
import com.octacore.rexpay.domain.models.PayPayload
import com.octacore.rexpay.ui.bankdetail.BankDetailScreen
import com.octacore.rexpay.ui.bankdetail.BankDetailViewModel
import com.octacore.rexpay.ui.banktransfer.BankTransferScreen
import com.octacore.rexpay.ui.banktransfer.BankTransferViewModel
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
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.SelectionScreen.route + "/{reference}",
    payload: PayPayload?,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavigationItem.SelectionScreen.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
                defaultValue = payload?.reference
            }
        )) {
            val factory = SelectionViewModel.provideFactory(RexPayApp.basePaymentRepo)
            val viewModel = viewModel<SelectionViewModel>(factory = factory)
            SelectionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(NavigationItem.CardDetailScreen.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory = CardDetailViewModel.provideFactory(RexPayApp.cardRepo)
            val viewModel: CardDetailViewModel = viewModel(factory = factory)
            CardDetailScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.OTPScreen.route) {
            OtpScreen(navController, payload)
        }
        composable(NavigationItem.BankTransferScreen.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory =
                BankTransferViewModel.provideFactory(RexPayApp.bankRepo, RexPayApp.basePaymentRepo)
            val viewModel: BankTransferViewModel = viewModel(factory = factory)
            BankTransferScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.BankDetailScreen.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory = BankDetailViewModel.provideFactory(RexPayApp.bankRepo)
            val viewModel: BankDetailViewModel = viewModel(factory = factory)
            BankDetailScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.USSDScreen.route + "/{reference}", arguments = listOf(
            navArgument("reference") {
                type = NavType.StringType
            }
        )) {
            val factory = USSDViewModel.provideFactory(RexPayApp.ussdRepo)
            val viewModel: USSDViewModel = viewModel(factory = factory)
            USSDScreen(navController = navController, vm = viewModel)
        }
        composable(NavigationItem.SuccessScreen.route + "/{amount}", arguments = listOf(
            navArgument("amount") {
                type = NavType.LongType
            }
        )) {backStackEntry ->
            val amount = backStackEntry.arguments?.getLong("amount", 0L)
            SuccessScreen(navController = navController, amount = amount)
        }
    }
}

internal enum class Screen {
    SELECTION,
    CARD_DETAIL,
    OTP,
    BANK_TRANSFER,
    BANK_TRANSFER_DETAIL,
    USSD,
    SUCCESS,
}

internal sealed class NavigationItem(val route: String) {
    data object SelectionScreen : NavigationItem(Screen.SELECTION.name)
    data object CardDetailScreen : NavigationItem(Screen.CARD_DETAIL.name)
    data object OTPScreen : NavigationItem(Screen.OTP.name)
    data object BankTransferScreen : NavigationItem(Screen.BANK_TRANSFER.name)
    data object BankDetailScreen : NavigationItem(Screen.BANK_TRANSFER_DETAIL.name)
    data object USSDScreen : NavigationItem(Screen.USSD.name)
    data object SuccessScreen : NavigationItem(Screen.SUCCESS.name)
}
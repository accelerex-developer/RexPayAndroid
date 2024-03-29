@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.globalaccelerex.rexpay.components.RexPayApp
import com.globalaccelerex.rexpay.ui.bankdetail.BankDetailScreen
import com.globalaccelerex.rexpay.ui.bankdetail.BankDetailViewModel
import com.globalaccelerex.rexpay.ui.banktransfer.BankTransferScreen
import com.globalaccelerex.rexpay.ui.banktransfer.BankTransferViewModel
import com.globalaccelerex.rexpay.ui.carddetail.CardDetailScreen
import com.globalaccelerex.rexpay.ui.carddetail.CardDetailViewModel
import com.globalaccelerex.rexpay.ui.otp.OtpScreen
import com.globalaccelerex.rexpay.ui.otp.OtpViewModel
import com.globalaccelerex.rexpay.ui.ussd.USSDScreen
import com.globalaccelerex.rexpay.ui.ussd.USSDViewModel

@Composable
internal fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.SelectionScreen.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavigationItem.SelectionScreen.route) { SelectionScreen(navController = navController) }
        composable(NavigationItem.CardDetailScreen.route) {
            val factory =
                CardDetailViewModel.provideFactory(RexPayApp.cardRepo, RexPayApp.basePaymentRepo)
            val viewModel = viewModel<CardDetailViewModel>(factory = factory)
            CardDetailScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.OTPScreen.route) {
            val factory =
                OtpViewModel.provideFactory(RexPayApp.cardRepo)
            val viewModel = viewModel<OtpViewModel>(factory = factory)
            OtpScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.BankTransferScreen.route) {
            val factory =
                BankTransferViewModel.provideFactory(RexPayApp.bankRepo, RexPayApp.basePaymentRepo)
            val viewModel = viewModel<BankTransferViewModel>(factory = factory)
            BankTransferScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.BankDetailScreen.route) {
            val factory = BankDetailViewModel.provideFactory(RexPayApp.bankRepo)
            val viewModel = viewModel<BankDetailViewModel>(factory = factory)
            BankDetailScreen(navController, vm = viewModel)
        }
        composable(NavigationItem.USSDScreen.route) {
            val factory =
                USSDViewModel.provideFactory(RexPayApp.ussdRepo, RexPayApp.basePaymentRepo)
            val viewModel = viewModel<USSDViewModel>(factory = factory)
            USSDScreen(navController = navController, vm = viewModel)
        }
        composable(NavigationItem.SuccessScreen.route) {
            SuccessScreen(navController = navController)
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
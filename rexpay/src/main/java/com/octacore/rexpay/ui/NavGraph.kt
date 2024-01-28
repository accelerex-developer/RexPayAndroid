package com.octacore.rexpay.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.octacore.rexpay.domain.BasePaymentRepo
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.carddetail.CardDetailScreen
import com.octacore.rexpay.ui.selection.SelectionScreen
import com.octacore.rexpay.ui.selection.SelectionViewModel
import com.octacore.rexpay.utils.LogUtils

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
fun AppNavGraph(
    activity: Activity,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Selection.route,
    payload: PayPayload?,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavigationItem.Selection.route) {
            val baseRepo = BasePaymentRepo.getInstance()
            val viewModel: SelectionViewModel =
                viewModel(factory = SelectionViewModel.provideFactory(baseRepo, payload))
            SelectionScreen(
                activity = activity,
                navHostController = navController,
                payload = payload,
                viewModel = viewModel
            )
        }
        composable(NavigationItem.CardDetail.route) {
            CardDetailScreen(navController, payload)
        }
    }
}

internal enum class Screen {
    SELECTION,
    CARD_DETAIL,
}

internal sealed class NavigationItem(val route: String) {
    data object Selection : NavigationItem(Screen.SELECTION.name)
    data object CardDetail : NavigationItem(Screen.CARD_DETAIL.name)
}
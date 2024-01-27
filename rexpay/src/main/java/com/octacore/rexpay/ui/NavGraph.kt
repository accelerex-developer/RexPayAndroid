package com.octacore.rexpay.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.ui.carddetail.CardDetailScreen

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 17/01/2024
 **************************************************************************************************/

@Composable
fun AppNavGraph(
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
            SelectionScreen(navController, payload)
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
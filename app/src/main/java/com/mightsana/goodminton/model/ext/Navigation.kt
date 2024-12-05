package com.mightsana.goodminton.model.ext

import androidx.navigation.NavHostController

fun NavHostController.navigateAndPopUpTo(
    destination: Any,
    current: Any
) {
    navigate(destination) {
        launchSingleTop = true
        popUpTo(current) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateAndClearBackStack(
    destination: Any
) {
    navigate(destination) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
    }
}

@Suppress("unused")
fun NavHostController.navigateSingleTop(
    destination: Any
) {
    navigate(destination) {
        launchSingleTop = true
    }
}
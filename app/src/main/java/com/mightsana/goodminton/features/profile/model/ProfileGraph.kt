package com.mightsana.goodminton.features.profile.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.mightsana.goodminton.features.profile.other_profile.OtherProfileScreen
import com.mightsana.goodminton.features.profile.self_profile.SelfProfileScreen

fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
) {
    composable(PROFILE) {
        SelfProfileScreen(
            navController = navController
        )
    }

    composable("$PROFILE/{uid}") { backStackEntry ->
        val uid = backStackEntry.arguments?.getString("uid")
        OtherProfileScreen(
            uid = uid!!,
            navController = navController
        )
    }

}

const val PROFILE = "Profile"
package com.mightsana.goodminton.features.profile.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mightsana.goodminton.features.profile.other_profile.OtherProfileScreen
import com.mightsana.goodminton.features.profile.self_profile.SelfProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class Profile(val uid: String? = null)

fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
) {
    composable<Profile> {
        val profile = it.toRoute<Profile>()
        if(profile.uid != null) {
            OtherProfileScreen(
                uid = profile.uid,
                navController = navController
            )
        } else {
            SelfProfileScreen(
                navController = navController
            )
        }
    }
}
//    composable(PROFILE) {
//        SelfProfileScreen(
//            navController = navController
//        )
//    }
//
//    composable("$PROFILE/{uid}") { backStackEntry ->
//        val uid = backStackEntry.arguments?.getString("uid")
//        OtherProfileScreen(
//            uid = uid!!,
//            navController = navController
//        )
//    }
//fun NavGraphBuilder.profileGraph(
//    navController: NavHostController,
//) {
//    composable<Profile>()
//    composable(PROFILE) {
//        SelfProfileScreen(
//            navController = navController
//        )
//    }
//
//    composable("$PROFILE/{uid}") { backStackEntry ->
//        val uid = backStackEntry.arguments?.getString("uid")
//        OtherProfileScreen(
//            uid = uid!!,
//            navController = navController
//        )
//    }
//
//}

const val PROFILE = "Profile"
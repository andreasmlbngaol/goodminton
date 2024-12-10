package com.mightsana.goodminton.features.profile.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mightsana.goodminton.Main
import com.mightsana.goodminton.features.auth.model.AuthGraph
import com.mightsana.goodminton.features.profile.edit_profile.EditProfileScreen
import com.mightsana.goodminton.features.profile.friend_list.FriendListScreen
import com.mightsana.goodminton.features.profile.other_profile.OtherProfileScreen
import com.mightsana.goodminton.features.profile.self_profile.SelfProfileScreen
import com.mightsana.goodminton.model.ext.navigateAndPopUpTo
import com.mightsana.goodminton.model.ext.navigateSingleTop
import kotlinx.serialization.Serializable

@Serializable data class Profile(val uid: String? = null)
@Serializable object EditProfile
@Serializable data class FriendList(val uid: String)

fun NavGraphBuilder.profileGraph(
    navController: NavHostController,
) {
    composable<Profile> {
        fun navigateToFriendList(uid: String) {
            navController.navigate(FriendList(uid))
        }

        val profile = it.toRoute<Profile>()
        if(profile.uid != null) {
            OtherProfileScreen(
                uid = profile.uid,
                onBack = { navController.navigateUp() },
                onNavigateToFriendList = { navigateToFriendList(it) }
            )
        } else {
            SelfProfileScreen(
                onBack = { navController.navigateUp() },
                onSignOut = { navController.navigateAndPopUpTo(AuthGraph, Main) },
                onNavigateToFriendList = { navigateToFriendList(it) },
                onNavigateToEditProfile = { navController.navigate(EditProfile) }
            )
        }
    }

    composable<EditProfile> {
        EditProfileScreen(
            onBack = { navController.navigateUp() }
        )
    }

    composable<FriendList> {
        val friendList = it.toRoute<FriendList>()
        FriendListScreen(
            uid = friendList.uid,
            onBack = { navController.navigateUp() },
            onNavigateToOtherProfile = { uid ->
                navController.navigateSingleTop(Profile(uid))
            }
        )
    }
}

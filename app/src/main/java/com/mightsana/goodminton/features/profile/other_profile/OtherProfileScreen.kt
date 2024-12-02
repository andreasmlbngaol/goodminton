package com.mightsana.goodminton.features.profile.other_profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfileScreen(
    uid: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OtherProfileViewModel = hiltViewModel()
) {
}
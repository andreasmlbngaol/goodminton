package com.mightsana.goodminton

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mightsana.goodminton.features.auth.model.authGraph
import com.mightsana.goodminton.features.main.detail.DetailContainer
import com.mightsana.goodminton.features.main.main.MainContainer
import com.mightsana.goodminton.features.maintenance.MaintenanceScreen
import com.mightsana.goodminton.features.maintenance.update.UpdateScreen
import com.mightsana.goodminton.features.profile.model.profileGraph
import kotlinx.serialization.Serializable

@Serializable object Maintenance
@Serializable object Update
@Serializable object Main
@Serializable data class League(val id: String)

@Composable
fun MyNavHost(
    navController: NavHostController,
    startDestination: Any,
    authStartDestination: Any
) {
    val defaultWebClientId = stringResource(R.string.default_web_client_id)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Maintenance> {
            MaintenanceScreen()
//            WeatherApp()
        }

        composable<Update> {
            UpdateScreen()
        }

        authGraph(
            navController = navController,
            startDestination = authStartDestination,
            mainRoute = Main,
            defaultWebClientId = defaultWebClientId
        )


        composable<Main> {
            MainContainer(navController = navController)
        }

        composable<League> {
            val league = it.toRoute<League>()
            DetailContainer(
                leagueId = league.id,
                onBack = { navController.navigateUp()}
            )
        }

        profileGraph(navController = navController)

    }
}
package com.mightsana.goodminton

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mightsana.goodminton.features.auth.model.authGraph
import com.mightsana.goodminton.features.main.detail.DetailContainer
import com.mightsana.goodminton.features.main.main.MainContainer
import com.mightsana.goodminton.features.maintenance.MaintenanceScreen
import com.mightsana.goodminton.features.profile.model.profileGraph
import kotlinx.serialization.Serializable

@Serializable object Maintenance
@Serializable object Main
@Serializable data class League(val id: String)

@Composable
fun MyNavHost(
    navController: NavHostController,
    startDestination: Any,
    authStartDestination: Any
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Maintenance> {
            MaintenanceScreen()
//            WeatherApp()
        }

        authGraph(
            navController = navController,
            startDestination = authStartDestination,
            mainRoute = Main,
            defaultWebClientId = getString(context, R.string.default_web_client_id)
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
package com.mightsana.goodminton.features.auth.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mightsana.goodminton.AUTH_GRAPH
import com.mightsana.goodminton.EMAIL_VERIFICATION
import com.mightsana.goodminton.REGISTER
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.SIGN_UP
import com.mightsana.goodminton.features.auth.email_verification.EmailVerificationScreen
import com.mightsana.goodminton.features.auth.register.RegisterScreen
import com.mightsana.goodminton.features.auth.sign_in.SignInScreen
import com.mightsana.goodminton.features.auth.sign_up.SignUpScreen

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    mainRoute: String,
    defaultWebClientId: String,
    startDestination: String = SIGN_IN
) {
    navigation(
        route = AUTH_GRAPH,
        startDestination = startDestination,
    ) {
        composable(SIGN_IN) {
            SignInScreen(
                navController = navController,
                mainRoute = mainRoute,
                defaultWebClientId = defaultWebClientId
            )
        }

        composable(SIGN_UP) {
            SignUpScreen(
                navController = navController,
                mainRoute = mainRoute,
                defaultWebClientId = defaultWebClientId
            )
        }

        composable(EMAIL_VERIFICATION) {
            EmailVerificationScreen(
                navController = navController
            )
        }

        composable(REGISTER) {
            RegisterScreen(
                navController = navController,
                mainRoute = mainRoute
            )
        }
    }

}

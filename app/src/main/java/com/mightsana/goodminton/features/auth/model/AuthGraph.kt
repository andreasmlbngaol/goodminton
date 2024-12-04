package com.mightsana.goodminton.features.auth.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mightsana.goodminton.features.auth.email_verification.EmailVerificationScreen
import com.mightsana.goodminton.features.auth.register.RegisterScreen
import com.mightsana.goodminton.features.auth.sign_in.SignInScreen
import com.mightsana.goodminton.features.auth.sign_up.SignUpScreen
import com.mightsana.goodminton.model.ext.navigateAndClearBackStack
import kotlinx.serialization.Serializable

@Serializable object AuthGraph
@Serializable object SignIn
@Serializable object SignUp
@Serializable object EmailVerification
@Serializable object Register

fun NavGraphBuilder.authGraphTypeSafe(
    navController: NavHostController,
    mainRoute: Any,
    defaultWebClientId: String,
) {
    navigation<AuthGraph>(startDestination = SignIn) {
        composable<SignIn> {
            SignInScreen(
                onCheckRegisterStatusAndNavigate = { navController.navigateAndClearBackStack(it ?: mainRoute) },
                onNavigateToSignUp = { navController.navigateAndClearBackStack(SignUp) },
                defaultWebClientId = defaultWebClientId
            )
        }

        composable<SignUp> {
            SignUpScreen(
                onSignUpWithEmailAndPassword = { navController.navigateAndClearBackStack(EmailVerification) },
                onSignInWithGoogle = { navController.navigateAndClearBackStack(it ?: mainRoute) },
                onNavigateToSignIn = { navController.navigateAndClearBackStack(SignIn) },
                defaultWebClientId = defaultWebClientId
            )
        }

        composable<EmailVerification> {
            EmailVerificationScreen(
                onSignOut = { navController.navigateAndClearBackStack(SignIn) },
                onEmailVerified = { navController.navigateAndClearBackStack(mainRoute) }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToSignIn = { navController.navigateAndClearBackStack(SignIn) },
                onRegister = { navController.navigateAndClearBackStack(mainRoute) }
            )
        }
    }

}

//fun NavGraphBuilder.authGraph(
//    navController: NavHostController,
//    mainRoute: String,
//    defaultWebClientId: String,
//    startDestination: String = SIGN_IN
//) {
//    navigation(
//        route = AUTH_GRAPH,
//        startDestination = startDestination,
//    ) {
//        composable(SIGN_IN) {
//            SignInScreen(
//                navController = navController,
//                defaultWebClientId = defaultWebClientId
//            )
//        }
//
//        composable(SIGN_UP) {
//            SignUpScreen(
//                navController = navController,
//                defaultWebClientId = defaultWebClientId
//            )
//        }
//
//        composable(EMAIL_VERIFICATION) {
//            EmailVerificationScreen(
//                navController = navController
//            )
//        }
//
//        composable(REGISTER) {
//            RegisterScreen(
//                navController = navController
//            )
//        }
//    }
//}


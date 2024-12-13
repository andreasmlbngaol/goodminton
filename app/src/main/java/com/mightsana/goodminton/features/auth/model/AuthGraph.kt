package com.mightsana.goodminton.features.auth.model

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mightsana.goodminton.features.auth.email_verification.EmailVerificationScreen
import com.mightsana.goodminton.features.auth.register.RegisterScreen
import com.mightsana.goodminton.features.auth.sign_in.SignInScreen
import com.mightsana.goodminton.features.auth.sign_up.SignUpScreen
import com.mightsana.goodminton.model.ext.navigateAndPopUpTo
import kotlinx.serialization.Serializable

@Serializable object AuthGraph
@Serializable object SignIn
@Serializable object SignUp
@Serializable object EmailVerification
@Serializable object Register

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    startDestination: Any,
    mainRoute: Any,
    defaultWebClientId: String,
) {
    navigation<AuthGraph>(startDestination = startDestination) {
        composable<SignIn> {
            SignInScreen(
                onCheckRegisterStatusAndNavigate = { navController.navigateAndPopUpTo(it ?: mainRoute, SignIn) },
                onNavigateToSignUp = { navController.navigateAndPopUpTo(SignUp, SignIn) },
                defaultWebClientId = defaultWebClientId
            )
        }

        composable<SignUp> {
            SignUpScreen(
                onSignUpWithEmailAndPassword = { navController.navigateAndPopUpTo(EmailVerification,
                    SignUp) },
                onSignInWithGoogle = { navController.navigateAndPopUpTo(it ?: mainRoute, SignUp) },
                onNavigateToSignIn = { navController.navigateAndPopUpTo(SignIn, SignUp) },
                defaultWebClientId = defaultWebClientId
            )
        }

        composable<EmailVerification> {
            EmailVerificationScreen(
                onSignOut = { navController.navigateAndPopUpTo(SignIn, EmailVerification) },
                onEmailVerified = { navController.navigateAndPopUpTo(Register, EmailVerification) }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToSignIn = { navController.navigateAndPopUpTo(SignIn, AuthGraph) },
                onRegister = { navController.navigateAndPopUpTo(mainRoute, Register) }
            )
        }
    }

}
package com.mightsana.goodminton.features.auth.model

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mightsana.goodminton.AUTH_GRAPH
import com.mightsana.goodminton.EMAIL_VERIFICATION
import com.mightsana.goodminton.REGISTER
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService

@Composable
fun AuthCheck(
    mainRoute: String,
    accountService: AccountService,
    appRepository: AppRepository,
    onAuthenticationResult: (String, String) -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            accountService.reloadUser()
            Log.d("AuthCheck", "User logged in: ${accountService.currentUser}")
            // userLoggedIn
            if (accountService.currentUser == null) {
                onAuthenticationResult(AUTH_GRAPH, SIGN_IN)
            } else if (!accountService.isEmailVerified()) {
                onAuthenticationResult(AUTH_GRAPH, EMAIL_VERIFICATION)
            } else if(!appRepository.isUserRegistered(accountService.currentUserId)) {
                onAuthenticationResult(AUTH_GRAPH, REGISTER)
            } else {
                onAuthenticationResult(mainRoute, REGISTER)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
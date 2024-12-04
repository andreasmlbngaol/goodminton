package com.mightsana.goodminton.features.auth.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService

@Composable
fun AuthCheck(
    mainRoute: Any,
    accountService: AccountService,
    appRepository: AppRepository,
    onAuthenticationResult: (Any, Any) -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            accountService.reloadUser()
            if (accountService.currentUser == null) {
                onAuthenticationResult(AuthGraph, SignIn)
            } else if (!accountService.isEmailVerified()) {
                onAuthenticationResult(AuthGraph, EmailVerification)
            } else if(!appRepository.isUserRegistered(accountService.currentUserId)) {
                onAuthenticationResult(AuthGraph, Register)
            } else {
                onAuthenticationResult(mainRoute, Register)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
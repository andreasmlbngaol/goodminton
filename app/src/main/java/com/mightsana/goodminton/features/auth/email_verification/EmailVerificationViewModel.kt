package com.mightsana.goodminton.features.auth.email_verification

import android.app.Application
import android.content.Intent
import com.mightsana.goodminton.features.auth.viewmodel.AuthViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application,
): AuthViewModel(accountService, appRepository, application) {
    private val _isEmailVerified = MutableStateFlow(false)

    val authEmail = accountService.currentUserEmail

    fun checkEmailVerification(
        onVerified: suspend () -> Unit,
    ) {
        launchCatching {
            while(!_isEmailVerified.value) {
                accountService.reloadUser()
                _isEmailVerified.value = accountService.isEmailVerified()
                delay(1000L)
            }
            onVerified()
        }
    }

    fun openEmailApp() {
        openOtherApp(
            category = Intent.CATEGORY_APP_EMAIL,
            packageName = "com.google.android.gm",
        )
    }
}

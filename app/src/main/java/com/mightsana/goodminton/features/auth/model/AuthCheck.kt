package com.mightsana.goodminton.features.auth.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.mightsana.goodminton.Maintenance
import com.mightsana.goodminton.Update
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService

@Composable
fun AuthCheck(
    mainRoute: Any,
    accountService: AccountService,
    appRepository: AppRepository,
    onAuthenticationResult: (Any, Any) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        try {
            accountService.reloadUser()
            var isVersionLatest = true
            val currentVersionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName!!.split(".")
            val latestVersionName = appRepository.getAppLatestVersionName().split(".")
            for (i in currentVersionName.indices) {
                if (currentVersionName[i].toInt() < latestVersionName[i].toInt()) {
                    isVersionLatest = false
                    break
                }
            }
            when {
                appRepository.isMaintenance() -> onAuthenticationResult(Maintenance, SignIn)
                !isVersionLatest -> onAuthenticationResult(Update, SignIn)
                accountService.currentUser == null -> onAuthenticationResult(AuthGraph, SignIn)
                !accountService.isEmailVerified() -> onAuthenticationResult(AuthGraph, EmailVerification)
                !appRepository.isUserRegistered(accountService.currentUserId) -> onAuthenticationResult(AuthGraph, Register)
                else -> onAuthenticationResult(mainRoute, SignIn)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
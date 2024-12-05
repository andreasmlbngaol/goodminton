package com.mightsana.goodminton.features.profile.self_profile

import android.app.Application
import com.mightsana.goodminton.features.profile.ProfileViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SelfProfileViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): ProfileViewModel(accountService, appRepository, application) {

    init {
        appLoading()
        observeUser(accountService.currentUserId)
        observeFriendsJoint(accountService.currentUserId) {
            appLoaded()
        }
    }

    private val _signOutDialogVisible = MutableStateFlow(false)
    val signOutDialogVisible = _signOutDialogVisible.asStateFlow()

    fun showSignOutDialog() {
        _signOutDialogVisible.value = true
    }

    fun dismissSignOutDialog() {
        _signOutDialogVisible.value = false
    }
}
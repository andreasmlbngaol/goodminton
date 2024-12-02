package com.mightsana.goodminton.features.profile.self_profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.features.profile.ProfileViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelfProfileViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): ProfileViewModel(accountService, appRepository, application) {

    init {
        appLoading()
        observeUser()
        observeFriendsUI(accountService.currentUserId)
        appLoaded()
//        viewModelScope.launch {
////            _friendCount.value = getFriendCount(accountService.currentUserId)
////            appLoaded()
//        }
    }

    fun refreshUserListener() {
        observeUser()
    }

}
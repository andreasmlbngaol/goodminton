package com.mightsana.goodminton.features.profile.other_profile

import android.app.Application
import com.mightsana.goodminton.features.profile.ProfileViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): ProfileViewModel(accountService, appRepository, application) {
    init {
        appLoading()
    }

    private val _otherUser = MutableStateFlow(MyUser())
    val otherUser = _otherUser.asStateFlow()

    private fun observeOtherUser(uid: String) {
        appRepository.observeUserJoint(uid) {
            _otherUser.value = it
        }
    }

    private val _dialogVisible = MutableStateFlow(false)
    val dialogVisible = _dialogVisible.asStateFlow()

    fun showDialog() {
        _dialogVisible.value = true
    }

    fun hideDialog() {
        _dialogVisible.value = false
    }


    fun observeOther(uid: String) {
        observeUser(accountService.currentUserId)
        observeOtherUser(uid)
        observeFriendsJoint(uid)
        appLoaded()
    }
}

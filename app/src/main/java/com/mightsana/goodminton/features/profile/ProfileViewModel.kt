package com.mightsana.goodminton.features.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.repository.friends.FriendUI
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class ProfileViewModel(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _profilePictureExpanded = MutableStateFlow(false)
    val profilePictureExpanded = _profilePictureExpanded.asStateFlow()

    protected val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    fun expandProfilePicture() {
        _profilePictureExpanded.value = true
    }

    fun dismissProfilePicture() {
        _profilePictureExpanded.value = false
    }

    protected val _friendsJoint = MutableStateFlow<List<FriendJoint>>(emptyList())
    val friendsJoint = _friendsJoint.asStateFlow()

    protected fun observeFriendsJoint(userId: String) {
        appRepository.observeFriendsJoint(userId) {
            viewModelScope.launch {
                _friendsJoint.value = it
            }
        }
    }

    protected fun observeUser(userId: String) {
        appRepository.observeUserJoint(userId) {
            viewModelScope.launch {
                _user.value = it
            }
        }
    }

}
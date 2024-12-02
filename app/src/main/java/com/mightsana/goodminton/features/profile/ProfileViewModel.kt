package com.mightsana.goodminton.features.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.model.repository.AppRepository
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

    protected val _friendsUI = MutableStateFlow<List<FriendUI>>(emptyList())
    val friendsUI = _friendsUI.asStateFlow()

    protected fun observeFriendsUI(userId: String) {
        appRepository.observeFriends(userId) { friendsList ->
            viewModelScope.launch {
                val friends = appRepository.getUsersByIds(
                    friendsList.map { friend ->
                        friend.ids.first { it != userId }
                    }
                )
                val friendsUI = friendsList.map { friend ->
                    val info = friends.find { it1 ->
                        it1.uid == friend.ids.first { it != userId }
                    }
                    info?.let { inf ->
                        FriendUI(
                            info = inf,
                            data = friend
                        )
                    }
                }
                _friendsUI.value = friendsUI.filterNotNull()
                Log.d("ProfileViewModel", "Friends UI: ${_friendsUI.value}")
            }
        }
    }



    protected fun observeUser() {
        appRepository.observeUser(accountService.currentUserId) {
            viewModelScope.launch {
                _user.value = it
            }
        }
    }

}
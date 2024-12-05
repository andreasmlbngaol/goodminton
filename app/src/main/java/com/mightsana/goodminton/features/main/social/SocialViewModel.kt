package com.mightsana.goodminton.features.main.social

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _allUsers = MutableStateFlow(listOf<MyUser>())
    val allUsers = _allUsers.asStateFlow()

    private fun getAllUsers() {
        viewModelScope.launch {
            _allUsers.value = appRepository.getAllUsers()
        }
    }
    private val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _friendRequestReceived = MutableStateFlow(listOf<FriendRequestJoint>())
    val friendRequestReceived = _friendRequestReceived.asStateFlow()

    private val _friendRequestSent = MutableStateFlow(listOf<FriendRequestJoint>())
//    val friendRequestSent = _friendRequestSent.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private fun setProcessing(value: Boolean) {
        _isProcessing.value = value
    }

    private fun isProcessing() {
        setProcessing(true)
    }

    private fun isNotProcessing() {
        setProcessing(false)
    }

    private fun observeFriendRequestReceived() {
        viewModelScope.launch {
            appRepository.observeFriendRequestsJoint(
                userId = accountService.currentUserId,
                onFriendRequestsSentUpdate = {
                    _friendRequestSent.value = it
                },
                onFriendRequestsReceivedUpdate = {
                    _friendRequestReceived.value = it
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun resetSearchQuery() {
        _searchQuery.value = ""
    }

    fun onSearchExpandedChange(expanded: Boolean) {
        if(!expanded) resetSearchQuery()
        _searchExpanded.value = expanded
    }

    fun collapseSearch() {
        onSearchExpandedChange(false)
    }

    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUserJoint(accountService.currentUserId) {
                _user.value = it
            }
        }
    }

    init {
        getAllUsers()
        observeUser()
        observeFriendRequestReceived()
    }

    fun acceptFriendRequest(requestId: String, senderId: String) {
        viewModelScope.launch {
            isProcessing()
            val userIds = listOf(accountService.currentUserId, senderId)
            appRepository.acceptFriendRequest(
                requestId = requestId,
                userIds = userIds
            )
            isNotProcessing()
        }
    }

    fun declineFriendRequest(requestId: String) {
        viewModelScope.launch {
            isProcessing()
            appRepository.deleteFriendRequest(requestId)
            isNotProcessing()
        }

    }
}
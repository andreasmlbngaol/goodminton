package com.mightsana.goodminton.features.profile.friend_list

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    private val _friends = MutableStateFlow(listOf<FriendJoint>())
    val friends = _friends.asStateFlow()

    private val _otherUser = MutableStateFlow(MyUser())
    val otherUser = _otherUser.asStateFlow()

    private val _otherFriends = MutableStateFlow(listOf<FriendJoint>())
    val otherFriends = _otherFriends.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _friendRequestReceived = MutableStateFlow(listOf<FriendRequestJoint>())
    val friendRequestReceived = _friendRequestReceived.asStateFlow()

    private val _friendRequestSent = MutableStateFlow(listOf<FriendRequestJoint>())
    val friendRequestSent = _friendRequestSent.asStateFlow()

    private val _unfriendDialogVisible = MutableStateFlow(false)
    val unfriendDialogVisible = _unfriendDialogVisible.asStateFlow()

    private val _unfriendUserId = MutableStateFlow<MyUser?>(null)
    val unfriendUserId = _unfriendUserId.asStateFlow()

    fun setUnfriendUser(value: MyUser?) {
        _unfriendUserId.value = value
    }

    fun showDialog() {
        _unfriendDialogVisible.value = true
    }

    fun dismissDialog() {
        _unfriendDialogVisible.value = false
    }

    private fun setProcessing(value: Boolean) {
        _isProcessing.value = value
    }

    private fun isProcessing() {
        setProcessing(true)
    }

    private fun isNotProcessing() {
        setProcessing(false)
    }

    private fun observeFriends() {
        appRepository.observeFriendsJoint(accountService.currentUserId) {
            _friends.value = it
        }
    }

    private fun observeOtherFriends(uid: String) {
        appRepository.observeFriendsJoint(uid) {
            _otherFriends.value = it
            appLoaded()
        }
    }

    private fun observeOtherUser(uid: String) {
        appRepository.observeUserJoint(uid) {
            _otherUser.value = it
        }
    }
    private fun observeUser() {
        appRepository.observeUserJoint(accountService.currentUserId) {
            _user.value = it
        }
    }

    fun observeOther(uid: String) {
        observeOtherUser(uid)
        observeOtherFriends(uid)
    }

    init {
        appLoading()
        observeFriends()
        observeUser()
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

    fun declineFriendRequest(senderId: String) {
        viewModelScope.launch {
            isProcessing()
            val requestId = _friendRequestReceived.value.find { it.sender.uid == senderId }!!.id
            appRepository.deleteFriendRequest(requestId)
            isNotProcessing()
        }
    }

    fun acceptFriendRequest(senderId: String) {
        viewModelScope.launch {
            isProcessing()
            val request = _friendRequestReceived.value.find { it.sender.uid == senderId }
            request?.let {
                appRepository.acceptFriendRequest(
                    requestId = it.id,
                    userIds = listOf(request.sender.uid, request.receiver.uid)
                )
            }
            isNotProcessing()
        }
    }

    fun cancelFriendRequest(receiverId: String) {
        viewModelScope.launch {
            isProcessing()
            val requestId = _friendRequestSent.value.find { it.receiver.uid == receiverId }!!.id
            appRepository.deleteFriendRequest(requestId)
            isNotProcessing()
        }
    }

    fun sendFriendRequest(receiverId: String) {
        viewModelScope.launch {
            isProcessing()
            appRepository.createFriendRequest(
                senderId = accountService.currentUserId,
                receiverId = receiverId
            )
            isNotProcessing()
        }
    }

    fun unfriend(uid: String) {
        viewModelScope.launch {
            isProcessing()
            val friendshipId = _friends.value.find { it.user.uid == uid }!!.id
            appRepository.deleteFriend(friendshipId)
            isNotProcessing()
        }
    }

}
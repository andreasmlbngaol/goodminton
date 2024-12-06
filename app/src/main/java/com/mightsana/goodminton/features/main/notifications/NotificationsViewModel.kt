package com.mightsana.goodminton.features.main.notifications

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.model.InvitationJoint
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _invitationsReceived = MutableStateFlow(listOf<InvitationJoint>())
    val invitationsReceived = _invitationsReceived.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private fun observeInvitationsReceived() {
        appRepository.observeLeagueInvitationsReceivedJoint(accountService.currentUserId) {
            _invitationsReceived.value = it
            appLoaded()
        }
    }

    fun acceptInvitation(invitationId: String, leagueId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            appRepository.acceptInvitation(invitationId, leagueId, accountService.currentUserId)
            _isProcessing.value = false
        }
    }

    fun declineInvitation(invitationId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            appRepository.deleteInvitation(invitationId)
            _isProcessing.value = false
        }
    }

    init {
        appLoading()
        observeInvitationsReceived()
    }
}
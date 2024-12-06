package com.mightsana.goodminton.features.main.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.model.InvitationJoint
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.MatchJoint
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    private val _friends = MutableStateFlow(listOf<FriendJoint>())
    val friends = _friends.asStateFlow()

    private val _matchesJoint = MutableStateFlow<List<MatchJoint>>(emptyList())
    val matchesJoint = _matchesJoint.asStateFlow()

    private val _invitationSent = MutableStateFlow<List<InvitationJoint>>(emptyList())
    val invitationSent = _invitationSent.asStateFlow()


    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUserJoint(accountService.currentUserId) {
                _user.value = it
            }
        }
    }

    private fun observeFriends() {
        viewModelScope.launch {
            appRepository.observeFriendsJoint(accountService.currentUserId) {
                _friends.value = it
            }
        }
    }

    private val _selectedItem = MutableStateFlow(0)
    val selectedItem: StateFlow<Int> = _selectedItem

    private val _leagueJoint = MutableStateFlow(LeagueJoint())
    val leagueJoint: StateFlow<LeagueJoint> = _leagueJoint

    private fun observeLeagueJoint(leagueId: String) {
        appRepository.observeLeagueJoint(leagueId) {
            _leagueJoint.value = it
        }
    }

    private val _leagueParticipantsJoint = MutableStateFlow(listOf<LeagueParticipantJoint>())
    val leagueParticipantsJoint = _leagueParticipantsJoint.asStateFlow()

    private fun observeLeagueParticipantsJoint(leagueId: String) {
        appRepository.observeLeagueParticipantsJoint(leagueId) { participants ->
            _leagueParticipantsJoint.value = participants
        }
    }

    private fun observeMatchesJoint(leagueId: String) {
        Log.d("DetailViewModel", "observeMatchesJoint: $leagueId")
        appRepository.observeMatchesJoint(leagueId) {
            _matchesJoint.value = it
            appLoaded()
        }
    }

    private fun observeInvitationSent(leagueId: String) {
        appRepository.observeLeagueInvitationsSentJoint(leagueId) {
            _invitationSent.value = it
        }
    }

    fun observeLeague(
        leagueId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            observeLeagueJoint(leagueId)
            observeLeagueParticipantsJoint(leagueId)
            observeInvitationSent(leagueId)
            observeMatchesJoint(leagueId)
            onSuccess()
        }
    }

    fun onSelectItem(index: Int) {
        _selectedItem.value = index
    }

    fun addParticipant(uid: String) {
        viewModelScope.launch {
            appRepository.addParticipant(_leagueJoint.value.id, uid)
        }
    }

    fun inviteFriend(uid: String) {
        viewModelScope.launch {
            appRepository.addInvitation(
                _leagueJoint.value.id,
                _user.value.uid,
                uid
            )
        }
    }

    fun cancelInvitation(invitationId: String) {
        viewModelScope.launch {
            appRepository.deleteInvitation(invitationId)
        }
    }

    fun acceptInvitation(invitationId: String, leagueId: String) {
        viewModelScope.launch {
            appRepository.acceptInvitation(invitationId, leagueId, accountService.currentUserId)
        }
    }

    fun declineInvitation(invitationId: String) {
        viewModelScope.launch {
            appRepository.deleteInvitation(invitationId)
        }
    }


    init {
        appLoading()
        observeUser()
        observeFriends()
    }

    // Participants
    fun changeParticipantRole(
        leagueId: String,
        userId: String,
        newRole: String
    ) {
        viewModelScope.launch {
            appRepository.updateParticipantRole(leagueId, userId, newRole)
        }
    }

    private val _participantsRoleExpanded = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val participantsRoleExpanded = _participantsRoleExpanded.asStateFlow()

    fun toggleParticipantsRoleExpanded(userId: String) {
        _participantsRoleExpanded.value = _participantsRoleExpanded.value.toMutableMap().apply {
            this[userId] = !(this[userId] ?: false)
        }
    }

    fun dismissParticipantsRoleExpanded(userId: String) {
        _participantsRoleExpanded.value = _participantsRoleExpanded.value.toMutableMap().apply {
            this[userId] = false
        }
    }

    private val _participantsSheetExpanded = MutableStateFlow(false)
    val participantsSheetExpanded = _participantsSheetExpanded.asStateFlow()

    fun dismissParticipantsSheet() {
        _participantsSheetExpanded.value = false
    }

    fun showParticipantsSheet() {
        _participantsSheetExpanded.value = true
    }


    // League Info
    fun updateLeagueDiscipline(newDouble: Boolean) {
        viewModelScope.launch {
            Log.d("DetailViewModel", "updateLeagueDiscipline: $newDouble")
            appRepository.updateLeagueDiscipline(_leagueJoint.value.id, newDouble)
        }
    }

    fun updateLeagueFixedDouble(newFixed: Boolean) {
        viewModelScope.launch {
            appRepository.updateLeagueFixedDouble(_leagueJoint.value.id, newFixed)
        }
    }

    fun updateLeagueDeuce(newDeuce: Boolean) {
        viewModelScope.launch {
            appRepository.updateLeagueDeuceEnabled(_leagueJoint.value.id, newDeuce)
        }
    }

    fun updateLeagueVisibility(newVisibility: Boolean) {
        viewModelScope.launch {
            appRepository.updateLeagueVisibility(_leagueJoint.value.id, newVisibility)
        }
    }

    private val _leagueName = MutableStateFlow("")
    val leagueName = _leagueName.asStateFlow()

    private val _changeNameDialogVisible = MutableStateFlow(false)
    val changeNameDialogVisible = _changeNameDialogVisible.asStateFlow()

    fun changeLeagueName(newName: String) {
        if(newName.length <= 16)
            _leagueName.value = newName
    }

    fun dismissChangeNameDialog() {
        _changeNameDialogVisible.value = false
    }

    fun showChangeNameDialog() {
        _changeNameDialogVisible.value = true
    }

    fun updateLeagueName() {
        if(_leagueName.value.isEmpty()) {
            dismissChangeNameDialog()
        } else {
            viewModelScope.launch {
                appRepository.updateLeagueName(_leagueJoint.value.id, _leagueName.value)
                dismissChangeNameDialog()
                delay(500)
                _leagueName.value = ""
            }
        }
    }

    private val _matchPoints = MutableStateFlow(0)
    val matchPoints = _matchPoints.asStateFlow()

    private val _changeMatchPointsDialogVisible = MutableStateFlow(false)
    val changeMatchPointsDialogVisible = _changeMatchPointsDialogVisible.asStateFlow()

    fun changeMatchPoints(newMatchPoints: String) {
        if(newMatchPoints.isEmpty()) {
            _matchPoints.value = 0
        } else {
            newMatchPoints.toIntOrNull()?.let {
                _matchPoints.value = it
            }
        }
    }

    fun dismissChangeMatchPointsDialog() {
        _changeMatchPointsDialogVisible.value = false
    }

    fun showChangeMatchPointsDialog() {
        _changeMatchPointsDialogVisible.value = true
    }

    fun updateLeagueMatchPoints() {
        if(_matchPoints.value == 0) {
            dismissChangeMatchPointsDialog()
        } else {
            viewModelScope.launch {
                appRepository.updateLeagueMatchPoints(_leagueJoint.value.id, _matchPoints.value)
                dismissChangeMatchPointsDialog()
                delay(500)
                _matchPoints.value = 0
            }
        }
    }

    private val _deleteLeagueDialogVisible = MutableStateFlow(false)
    val deleteLeagueDialogVisible = _deleteLeagueDialogVisible.asStateFlow()

    fun dismissDeleteLeagueDialog() {
        _deleteLeagueDialogVisible.value = false
    }

    fun showDeleteLeagueDialog() {
        _deleteLeagueDialogVisible.value = true
    }

    fun deleteLeague(
        onNavigateToHome: () -> Unit
    ) {
        viewModelScope.launch {
            appRepository.deleteLeague(_leagueJoint.value.id)
            onNavigateToHome()
            appLoading()
            dismissDeleteLeagueDialog()
            appLoaded()
        }
    }

}
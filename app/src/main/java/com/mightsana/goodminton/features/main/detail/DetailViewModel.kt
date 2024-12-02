package com.mightsana.goodminton.features.main.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantUI
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.model.repository.AppRepository
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

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUser(accountService.currentUserId) {
                _user.value = it
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

    private val _leagueParticipantsUI = MutableStateFlow(listOf<LeagueParticipantUI>())
    val leagueParticipantsUI = _leagueParticipantsUI.asStateFlow()

    private val _leagueParticipantsJoint = MutableStateFlow(listOf<LeagueParticipantJoint>())
    val leagueParticipantsJoint = _leagueParticipantsJoint.asStateFlow()

    private fun observeLeagueParticipantsJoint(leagueId: String) {
        appRepository.observeLeagueParticipantsJoint(leagueId) { participants ->
            _leagueParticipantsJoint.value = participants
        }
    }

    private fun observeLeagueParticipantsUI(leagueId: String) {
        appRepository.observeLeagueParticipants(leagueId) { participantsIds ->
            viewModelScope.launch {
                val users = appRepository.getUsersByIds(participantsIds.map { it.userId })
                val stats = appRepository.getParticipantStatsByParticipantIds(participantsIds.map { it.userId })
                val participantsUI = participantsIds.map { participant ->
                    val user = users.find { it.uid == participant.userId }
                    val stat = stats.find { it.userId == participant.userId }
                    user?.let { usr ->
                        stat?.let { stt ->
                            LeagueParticipantUI(participant, usr, stt)
                        }
                    }
                }
                _leagueParticipantsUI.value = participantsUI.filterNotNull()
            }
        }
    }


    private fun observeMatches(leagueId: String) {
        appRepository.observeMatches(leagueId) {
            _matches.value = it
        }
    }
    fun observeLeague(leagueId: String) {
//        observeLeagueInfo(leagueId)
        observeLeagueJoint(leagueId)
        observeLeagueParticipantsJoint(leagueId)
//        observeLeagueParticipantsUI(leagueId)
        observeMatches(leagueId)
    }

    fun onSelectItem(index: Int) {
        _selectedItem.value = index
    }

    init {
        observeUser()
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
            dismissDeleteLeagueDialog()
            onNavigateToHome()
        }
    }

}
package com.mightsana.goodminton.features.main.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueParticipantsUI
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _selectedItem = MutableStateFlow(0)
    val selectedItem: StateFlow<Int> = _selectedItem

    private val _leagueInfo = MutableStateFlow(League())
    val leagueInfo: StateFlow<League> = _leagueInfo

    private val _leagueParticipantsUI = MutableStateFlow(listOf<LeagueParticipantsUI>())
    val leagueParticipantsUI = _leagueParticipantsUI.asStateFlow()

    private fun observeLeagueParticipantsUI(leagueId: String) {
        appRepository.observeLeagueParticipants(leagueId) { participantsIds ->
            viewModelScope.launch {
                val users = appRepository.getUsersByIds(participantsIds.map { it.userId })
                Log.d("DetailViewModel", "users: $users")
                val stats = appRepository.getStatsByUserIds(participantsIds.map { it.userId })
                Log.d("DetailViewModel", "stats: $stats")
                val participantsUI = participantsIds.map { participant ->
                    val user = users.find { it.uid == participant.userId }
                    val stat = stats.find { it.userId == participant.userId }
                    user?.let { usr ->
                        stat?.let { stt ->
                            LeagueParticipantsUI(participant, usr, stt)
                        }
                    }
                }
                _leagueParticipantsUI.value = participantsUI.filterNotNull()
            }
        }
    }


    private fun observeLeagueInfo(leagueId: String) {
        appRepository.observeLeagueInfo(leagueId) {
            _leagueInfo.value = it

        }
    }

    fun observeLeague(leagueId: String) {
        observeLeagueInfo(leagueId)
        observeLeagueParticipantsUI(leagueId)
    }

    fun onSelectItem(index: Int) {
        _selectedItem.value = index
    }

}
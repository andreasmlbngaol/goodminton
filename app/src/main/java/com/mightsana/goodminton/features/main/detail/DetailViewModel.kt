package com.mightsana.goodminton.features.main.detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueParticipants
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.users.MyUser
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

    private val _leagueParticipants = MutableStateFlow(listOf<MyUser>())
    val leagueParticipants = _leagueParticipants.asStateFlow()

    private val _participationInfo = MutableStateFlow(listOf<LeagueParticipants>())
    val participationInfo = _participationInfo.asStateFlow()

    private fun observeLeagueInfo(leagueId: String) {
        appRepository.observeLeagueInfo(leagueId) {
            _leagueInfo.value = it

        }
    }

    private fun getLeagueParticipantsUser(leagueParticipants: List<LeagueParticipants>) {
        viewModelScope.launch {
            _leagueParticipants.value = appRepository.getUsersByIds(
                leagueParticipants.map { it.userId }
            )
        }
    }

    private fun observeLeagueParticipants(leagueId: String) {
        appRepository.observeLeagueParticipants(leagueId) {
            _participationInfo.value = it
            getLeagueParticipantsUser(it)
        }
    }

    fun observeLeague(leagueId: String) {
        observeLeagueInfo(leagueId)
        observeLeagueParticipants(leagueId)
    }

    fun onSelectItem(index: Int) {
        _selectedItem.value = index
    }

}
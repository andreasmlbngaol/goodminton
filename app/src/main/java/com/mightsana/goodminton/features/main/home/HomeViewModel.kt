package com.mightsana.goodminton.features.main.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.features.main.main.FormValidationResult
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.model.ext.clip
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    private val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    private val _leagues = MutableStateFlow<List<League>>(emptyList())
    val leagues = _leagues.asStateFlow()

    private val _searchExpanded = MutableStateFlow(false)
    val searchExpanded = _searchExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _bottomSheetExpanded = MutableStateFlow(false)
    val bottomSheetExpanded = _bottomSheetExpanded.asStateFlow()

    private val _leagueName = MutableStateFlow("")
    val leagueName = _leagueName.asStateFlow()

    private val _matchPoints = MutableStateFlow(0)
    val matchPoints = _matchPoints.asStateFlow()

    private val _deuceEnabled = MutableStateFlow(true)
    val deuceEnabled = _deuceEnabled.asStateFlow()

    private val _isDouble = MutableStateFlow(true)
    val isDouble = _isDouble.asStateFlow()

    private val _isFixedDouble = MutableStateFlow(false)
    val isFixedDouble = _isFixedDouble.asStateFlow()

    private val _nameErrorMessage = MutableStateFlow<String?>(null)
    val nameErrorMessage = _nameErrorMessage.asStateFlow()

    private val _matchPointsErrorMessage = MutableStateFlow<String?>(null)
    val matchPointsErrorMessage = _matchPointsErrorMessage.asStateFlow()

    private val _isPrivate = MutableStateFlow(true)
    val isPrivate = _isPrivate.asStateFlow()

    fun togglePrivate() {
        _isPrivate.value = !_isPrivate.value
    }

    fun updateLeagueName(name: String) {
        _leagueName.value = name
    }

    fun updateMatchPoints(points: String) {
        if(points.isEmpty())
            _matchPoints.value = 0
        else
            points.toIntOrNull()?.let {
                _matchPoints.value = it
            }
    }

    fun toggleDeuce() {
        _deuceEnabled.value = !_deuceEnabled.value
    }

    fun toggleFixedDouble() {
        _isFixedDouble.value = !_isFixedDouble.value
    }

    fun toggleDouble() {
        _isDouble.value = !_isDouble.value
    }

    fun resetForm() {
        _leagueName.value = ""
        _matchPoints.value = 0
        _deuceEnabled.value = true
        _isDouble.value = true
        _isFixedDouble.value = false
        resetErrors()
    }
    fun onBottomSheetExpandedChange(expanded: Boolean) {
        _bottomSheetExpanded.value = expanded
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun resetSearchQuery() {
        _searchQuery.value = ""
    }

    fun onSearchExpandedChange(expanded: Boolean) {
        if(!expanded) resetSearchQuery()
        _searchExpanded.value = expanded
    }

    fun collapseSearch() {
        onSearchExpandedChange(false)
    }

    private fun setNameErrorMessage(message: String?) {
        _nameErrorMessage.value = message
    }

    private fun setMatchPointsErrorMessage(message: String?) {
        _matchPointsErrorMessage.value = message
    }

    fun resetErrors() {
        setNameErrorMessage(null)
        setMatchPointsErrorMessage(null)
    }

    private fun validateNewLeagueForm(
        onSuccess: () -> Unit
    ) {
        resetErrors()
        val validationState = when {
            _leagueName.value.isBlank() -> FormValidationResult.NewLeagueResult.NameError("League name cannot be empty")
            _matchPoints.value == 0 -> FormValidationResult.NewLeagueResult.MatchPointsError("Match points cannot be empty")
            else -> FormValidationResult.Valid
        }

        when(validationState) {
            is FormValidationResult.NewLeagueResult.NameError -> setNameErrorMessage(validationState.message)
            is FormValidationResult.NewLeagueResult.MatchPointsError -> setMatchPointsErrorMessage(validationState.message)
            else -> onSuccess()
        }
    }

    fun addLeague(
        onSuccess: () -> Unit
    ) {
        validateNewLeagueForm {
            viewModelScope.launch {
                appRepository.createNewLeague(
                    League(
                        name = _leagueName.clip(),
                        matchPoints = _matchPoints.value,
                        deuceEnabled = _deuceEnabled.value,
                        double = _isDouble.value,
                        private = _isPrivate.value,
                        fixedDouble = if (_isDouble.value) _isFixedDouble.value else null,
                        createdById = _user.value.uid
                    )
                )
                toast("${_leagueName.clip()} Added!")
                loadLeagues()
                onSuccess()
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUser(accountService.currentUserId) {
                _user.value = it
            }
        }
    }

    fun loadLeagues() {
        viewModelScope.launch {
            _leagues.value = appRepository.getLeaguesByUserId(accountService.currentUserId)
        }
    }

    init {
        observeUser()
        loadLeagues()
    }
}
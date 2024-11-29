package com.mightsana.goodminton.features.main.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
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

    private val _isDouble = MutableStateFlow(false)
    val isDouble = _isDouble.asStateFlow()

    private val _isFixedDouble = MutableStateFlow(true)
    val isFixedDouble = _isFixedDouble.asStateFlow()

    fun updateLeagueName(name: String) {
        _leagueName.value = name
    }

    fun updateMatchPoints(points: String) {
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
        _isDouble.value = false
        _isFixedDouble.value = true
    }
    fun onBottomSheetExpandedChange(expanded: Boolean) {
        _bottomSheetExpanded.value = expanded
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchExpandedChange(expanded: Boolean) {
        _searchExpanded.value = expanded
    }

    fun collapseSearch() {
        _searchExpanded.value = false
    }

    fun addLeague() {
//        appRepository.addNewLeague()
    }

    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUser(accountService.currentUserId) {
                _user.value = it
            }
        }
    }

    init {
        observeUser()
    }
}
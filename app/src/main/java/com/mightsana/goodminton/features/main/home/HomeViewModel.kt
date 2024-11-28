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

    private val _selectedItem = MutableStateFlow("Home")
    val selectedItem = _selectedItem.asStateFlow()

    fun onSelectItem(item: String) {
        _selectedItem.value = item
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

    private val _friendRequestReceivedCount = MutableStateFlow(0)
    val friendRequestReceivedCount = _friendRequestReceivedCount.asStateFlow()

    private fun observeUser() {
        viewModelScope.launch {
            appRepository.observeUser(accountService.currentUserId) {
                _user.value = it
            }
        }
    }

    init {
        observeUser()
        appRepository.observeFriendRequestReceivedCount(accountService.currentUserId) {
            _friendRequestReceivedCount.value = it
        }
    }
}
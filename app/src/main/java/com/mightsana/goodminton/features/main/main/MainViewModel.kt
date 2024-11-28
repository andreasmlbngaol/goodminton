package com.mightsana.goodminton.features.main.main

import android.app.Application
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {

    private val _selectedItem = MutableStateFlow("Home")
    val selectedItem = _selectedItem.asStateFlow()

    fun onSelectItem(item: String) {
        _selectedItem.value = item
    }

    private val _friendRequestReceivedCount = MutableStateFlow(0)
    val friendRequestReceivedCount = _friendRequestReceivedCount.asStateFlow()

    init {
        appRepository.observeFriendRequestReceivedCount(accountService.currentUserId) {
            _friendRequestReceivedCount.value = it
        }
    }
}
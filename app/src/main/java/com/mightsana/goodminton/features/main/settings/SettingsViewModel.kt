package com.mightsana.goodminton.features.main.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jakewharton.processphoenix.ProcessPhoenix
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.PREF_DYNAMIC_COLOR
import com.mightsana.goodminton.PREF_NAME
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    val sharedPreferences: SharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val currentDynamicColorEnabled = sharedPreferences.getBoolean(PREF_DYNAMIC_COLOR, false)

    private val _dynamicColorEnabled = MutableStateFlow(currentDynamicColorEnabled)
    val dynamicColorEnabled = _dynamicColorEnabled.asStateFlow()

    fun setDynamicColorEnabled(enabled: Boolean) {
        _dynamicColorEnabled.value = enabled
        sharedPreferences.edit().putBoolean(PREF_DYNAMIC_COLOR, enabled).apply()
        setShowSnackbar(_dynamicColorEnabled.value != currentDynamicColorEnabled)
    }

    private val _showSnackbar = MutableStateFlow(false)
    val showSnackbar = _showSnackbar.asStateFlow()

    fun setShowSnackbar(value: Boolean) {
        _showSnackbar.value = value
    }

    fun restartApp() {
        ProcessPhoenix.triggerRebirth(application)
    }

}
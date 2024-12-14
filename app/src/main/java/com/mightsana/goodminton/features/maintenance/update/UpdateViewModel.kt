package com.mightsana.goodminton.features.maintenance.update

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.MyViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): MyViewModel(accountService, appRepository, application) {
    val currentVersionName = application.packageManager.getPackageInfo(application.packageName, 0).versionName
    private val _latestVersionName = MutableStateFlow("")
    val latestVersionName = _latestVersionName.asStateFlow()

    init {
        viewModelScope.launch {
            appLoading()
            _latestVersionName.value = appRepository.getAppLatestVersionName()
            appLoaded()
        }
    }

    fun openUrl() {
        openUrl(application.getString(R.string.releases_url))
    }
}
package com.mightsana.goodminton.features.profile.other_profile

import android.app.Application
import com.mightsana.goodminton.features.profile.ProfileViewModel
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
): ProfileViewModel(accountService, appRepository, application) {

}

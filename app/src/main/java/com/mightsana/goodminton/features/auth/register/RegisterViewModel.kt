package com.mightsana.goodminton.features.auth.register

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.auth.viewmodel.AuthViewModel
import com.mightsana.goodminton.features.auth.viewmodel.FormValidationResult
import com.mightsana.goodminton.model.ext.noSpace
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application,
) : AuthViewModel(accountService, appRepository, application) {

    private val _fullName = MutableStateFlow(accountService.currentUser!!.displayName ?: "")
    val fullName = _fullName.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _isFullNameError = MutableStateFlow(false)
    val isFullNameError = _isFullNameError.asStateFlow()

    private val _isNicknameError = MutableStateFlow(false)
    val isNicknameError = _isNicknameError.asStateFlow()

    private val _isUsernameError = MutableStateFlow(false)
    val isUsernameError = _isUsernameError.asStateFlow()

    private val _fullNameErrorMessage = MutableStateFlow<String?>(null)
    val fullNameErrorMessage = _fullNameErrorMessage.asStateFlow()

    private val _nicknameErrorMessage = MutableStateFlow<String?>(null)
    val nicknameErrorMessage = _nicknameErrorMessage.asStateFlow()

    private val _usernameErrorMessage = MutableStateFlow<String?>(null)
    val usernameErrorMessage = _usernameErrorMessage.asStateFlow()

    private fun validateFullName() {
        if(isFullNameBlank()) setFullNameErrorMessage(application.getString(R.string.full_name_blank))
        else if(!isFullNameValid()) setFullNameErrorMessage(application.getString(R.string.full_name_invalid))
        else setFullNameErrorMessage(null)
    }

    fun updateFullName(fullName: String) {
        _fullName.value = fullName
        validateFullName()
    }

    private fun validateNickname() {
        if(isNicknameBlank()) setNicknameErrorMessage(application.getString(R.string.nickname_blank))
        else if(!isNicknameValid()) setNicknameErrorMessage(application.getString(R.string.nickname_invalid))
        else setNicknameErrorMessage(null)
    }

    fun updateNickname(nickname: String) {
        _nickname.value = nickname
        validateNickname()
    }

    private fun isUsernameValid(): Boolean {
        return Regex("^[a-z0-9_.]+$").matches(_username.value)
    }

    private fun isFullNameValid(): Boolean {
        return Regex("^[a-zA-Z ]+$").matches(_fullName.value)
    }

    private fun isNicknameBlank(): Boolean {
        return _nickname.value.isBlank()
    }

    private fun isNicknameValid(): Boolean {
        return Regex("^[a-zA-Z]+$").matches(_nickname.value)
    }

    private fun validateUsername(
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (isUsernameBlank()) {
                    setUsernameErrorMessage(application.getString(R.string.username_blank))
                } else if (!isUsernameValid()) {
                    setUsernameErrorMessage(application.getString(R.string.username_invalid))
                } else if (_username.value.length < 6) {
                    setUsernameErrorMessage(application.getString(R.string.username_length_invalid))
                } else if (!appRepository.isUsernameAvailable(_username.value)) {
                    setUsernameErrorMessage(application.getString(R.string.username_taken))
                } else {
                    setUsernameErrorMessage(null)
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error checking username availability", e)
            }
        }
    }

    fun updateUsername(username: String) {
        _username.value = username.lowercase().noSpace()
        validateUsername()
    }

    private fun setFullNameError(error: Boolean) {
        _isFullNameError.value = error
    }

    private fun setNicknameError(error: Boolean) {
        _isNicknameError.value = error
    }

    private fun setUsernameError(error: Boolean) {
        _isUsernameError.value = error
    }

    private fun setFullNameErrorMessage(message: String?) {
        _fullNameErrorMessage.value = message
        if(!message.isNullOrBlank()) setFullNameError(true) else setFullNameError(false)
    }

    private fun setNicknameErrorMessage(message: String?) {
        _nicknameErrorMessage.value = message
        if(!message.isNullOrBlank()) setNicknameError(true) else setNicknameError(false)
    }

    private fun setUsernameErrorMessage(message: String?) {
        _usernameErrorMessage.value = message
        if(!message.isNullOrBlank()) setUsernameError(true) else setUsernameError(false)
    }

    private fun isFullNameBlank(): Boolean {
        return _fullName.value.isBlank()
    }

    private fun isUsernameBlank(): Boolean {
        return _username.value.isBlank()
    }

    override fun resetErrors() {
        setFullNameErrorMessage(null)
        setNicknameErrorMessage(null)
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            appRepository.createNewUser(
                MyUser(
                    uid = accountService.currentUserId,
                    name = _fullName.value,
                    nickname = _nickname.value,
                    username = _username.value,
                    email = accountService.currentUserEmail,
                    profilePhotoUrl = accountService.currentProfilePhotoUrl ?: "https://lh3.googleusercontent.com/a/ACg8ocJqioouAGDYYtGzu3L9ZQ5GGWj9JOaUC4XN_7hk9PLDy3gIQPs=s360-c-no",
                    createdAt = accountService.createdTimestamp
                )
            )
            onSuccess()
        }
    }

    fun validateRegisterForm(onValid: () -> Unit) {
        resetErrors()
        val validationState = when {
            isFullNameBlank() -> FormValidationResult.RegisterResult.FullNameError(application.getString(R.string.full_name_blank))
            !isFullNameValid() -> FormValidationResult.RegisterResult.FullNameError(application.getString(R.string.full_name_invalid))
            isNicknameBlank() -> FormValidationResult.RegisterResult.NicknameError(application.getString(R.string.nickname_blank))
            !isNicknameValid() -> FormValidationResult.RegisterResult.NicknameError(application.getString(R.string.nickname_invalid))
            isUsernameBlank() -> FormValidationResult.RegisterResult.UsernameError(application.getString(R.string.username_blank))
            !isUsernameValid() -> FormValidationResult.RegisterResult.UsernameError(application.getString(R.string.username_invalid))
            else -> FormValidationResult.Valid
        }

        when(validationState) {
            is FormValidationResult.RegisterResult.FullNameError -> setFullNameErrorMessage(validationState.message)
            is FormValidationResult.RegisterResult.NicknameError -> setNicknameErrorMessage(validationState.message)
            is FormValidationResult.RegisterResult.UsernameError -> setUsernameErrorMessage(validationState.message)
            else -> validateUsername {
                onValid()
            }
        }
    }
}
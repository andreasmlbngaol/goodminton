package com.mightsana.goodminton.features.profile.edit_profile

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
class EditProfileViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application
) : MyViewModel(accountService, appRepository, application) {
    private val _user = MutableStateFlow(MyUser())
    val user = _user.asStateFlow()

    private val _name = MutableStateFlow<String?>(null)
    val name = _name.asStateFlow()

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname = _nickname.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()

    private val _bio = MutableStateFlow<String?>(null)
    val bio = _bio.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private fun setEditing() { _isEditing.value = true }

    private val _usernameErrorMessage = MutableStateFlow<String?>(null)
    val usernameErrorMessage = _usernameErrorMessage.asStateFlow()

    fun updateName(newName: String) {
        if(newName.length <= 20) {
            _name.value = newName
            setEditing()
        }
    }

    fun updateNickname(newNickname: String) {
        if(newNickname.length <= 10) {
            _nickname.value = newNickname
            setEditing()
        }
    }

    fun updateUsername(newUsername: String) {
        if(newUsername.length <= 16) {
            _username.value = newUsername
            setEditing()
            viewModelScope.launch {
                if (appRepository.isUsernameAvailable(newUsername) || newUsername == _user.value.username) {
                    _usernameErrorMessage.value = null
                } else {
                    _usernameErrorMessage.value = "Username is taken"
                }
            }
        }
    }

    fun updateBio(newBio: String) {
        if(newBio.length <= 100) {
            _bio.value = newBio
            setEditing()
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val name = if (_name.value.isNullOrBlank()) _user.value.name else _name.value.orEmpty()
            val nickname = if (_nickname.value.isNullOrBlank()) _user.value.nickname else _nickname.value.orEmpty()
            val username = if (_username.value.isNullOrBlank()) _user.value.username else _username.value.orEmpty()
            val bio = if (_bio.value.isNullOrBlank()) _user.value.bio else _bio.value.orEmpty()
            val editedUser = _user.value.copy(
                name = name,
                nickname = nickname,
                username = username,
                bio = bio
            )

            appRepository.editUser(editedUser) {
                onSuccess()
            }
        }
    }

    private fun observeUser() {
        appRepository.observeUserJoint(accountService.currentUserId) {
            _user.value = it
        }
    }

    init {
        observeUser()
    }
}
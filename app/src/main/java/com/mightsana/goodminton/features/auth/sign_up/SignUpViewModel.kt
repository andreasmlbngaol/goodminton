package com.mightsana.goodminton.features.auth.sign_up

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.auth.viewmodel.AuthViewModel
import com.mightsana.goodminton.features.auth.viewmodel.FormValidationResult
import com.mightsana.goodminton.model.ext.clip
import com.mightsana.goodminton.model.ext.toast
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application,
): AuthViewModel(accountService, appRepository, application) {

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword.replace(" ", "")
    }

    private val _confirmPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = _confirmPasswordVisible.asStateFlow()

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = !_confirmPasswordVisible.value
    }

    private val _isConfirmPasswordError = MutableStateFlow(false)
    val isConfirmPasswordError = _isConfirmPasswordError.asStateFlow()

    private fun setConfirmPasswordError(error: Boolean) {
        _isConfirmPasswordError.value = error
    }

    private val _confirmPasswordErrorMessage = MutableStateFlow<String?>(null)
    val confirmPasswordErrorMessage = _confirmPasswordErrorMessage.asStateFlow()

    private fun setConfirmPasswordErrorMessage(message: String?) {
        _confirmPasswordErrorMessage.value = message
        if(!message.isNullOrBlank()) setConfirmPasswordError(true) else setConfirmPasswordError(false)
    }

    private fun resetConfirmPasswordError() {
        setConfirmPasswordErrorMessage(null)
    }

    override fun resetErrors() {
        super.resetErrors()
        resetConfirmPasswordError()
    }

    fun isConfirmPasswordBlank() = _confirmPassword.value.isBlank()
    fun isConfirmPasswordMatch() = _password.value == _confirmPassword.value


    fun validateSignUpForm(
        onSuccess: () -> Unit
    ) {
        resetErrors()
        val validationState = when {
            isEmailBlank()-> FormValidationResult.SignUpResult.EmailError(application.getString(R.string.email_blank))
            !isEmailValid() -> FormValidationResult.SignUpResult.EmailError(application.getString(R.string.email_invalid))
            isPasswordBlank() -> FormValidationResult.SignUpResult.PasswordError(application.getString(R.string.password_blank))
            !isPasswordLengthValid() -> FormValidationResult.SignUpResult.PasswordError(application.getString(R.string.password_length_invalid))
            !isPasswordHasNumber() -> FormValidationResult.SignUpResult.PasswordError(application.getString(R.string.password_number_invalid))
            !isPasswordHasUpperCase() -> FormValidationResult.SignUpResult.PasswordError(application.getString(R.string.password_uppercase_invalid))
            !isPasswordHasLowerCase() -> FormValidationResult.SignUpResult.PasswordError(application.getString(R.string.password_lowercase_invalid))
            isConfirmPasswordBlank() -> FormValidationResult.SignUpResult.ConfirmPasswordError(application.getString(R.string.confirm_password_blank))
            !isConfirmPasswordMatch() -> FormValidationResult.SignUpResult.ConfirmPasswordError(application.getString(R.string.confirm_password_not_match))
            else -> FormValidationResult.Valid
        }

        when(validationState) {
            is FormValidationResult.SignUpResult.EmailError -> setEmailErrorMessage(validationState.message)
            is FormValidationResult.SignUpResult.PasswordError -> setPasswordErrorMessage(validationState.message)
            is FormValidationResult.SignUpResult.ConfirmPasswordError -> setConfirmPasswordErrorMessage(validationState.message)
            else -> onSuccess()
        }
    }

    fun onSignUpWithEmailAndPassword(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                appLoading()
                accountService.signUpWithEmailAndPassword(
                    email = _email.clip(),
                    password = _password.clip()
                )
                while (accountService.currentUser == null) {
                    delay(500)
                }
                accountService.sendEmailVerification()
                Log.d("SignUpViewModel", "Email Sent")
                onSuccess()
            } catch (e: FirebaseNetworkException) {
                application.toast(e.message.toString())
            } catch (e: Exception) {
                Log.e("SignUpViewModel", e.message.toString())
                application.toast(R.string.sign_up_error)
            } finally {
                appLoaded()
            }
        }
    }
}
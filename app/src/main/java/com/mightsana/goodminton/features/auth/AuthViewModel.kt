package com.mightsana.goodminton.features.auth

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.mightsana.goodminton.OneViewModel
import com.mightsana.goodminton.REGISTER
import com.mightsana.goodminton.model.ext.clip
import com.mightsana.goodminton.model.ext.noSpace
import com.mightsana.goodminton.model.ext.toast
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Suppress("unused")
abstract class AuthViewModel(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application,
): OneViewModel(accountService, appRepository, application) {
    protected val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    open fun updateEmail(email: String) {
        _email.value = email.lowercase().noSpace()
    }

    protected val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    open fun updatePassword(password: String) {
        _password.value = password.noSpace()
    }

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    private val _isEmailError = MutableStateFlow(false)
    val isEmailError = _isEmailError.asStateFlow()

    private fun setEmailError(error: Boolean) {
        _isEmailError.value = error
    }

    private val _isPasswordError = MutableStateFlow(false)
    val isPasswordError = _isPasswordError.asStateFlow()

    private fun setPasswordError(error: Boolean) {
        _isPasswordError.value = error
    }

    private val _emailErrorMessage = MutableStateFlow<String?>(null)
    val emailErrorMessage = _emailErrorMessage.asStateFlow()

    protected fun setEmailErrorMessage(message: String?) {
        _emailErrorMessage.value = message
        if(!message.isNullOrBlank()) setEmailError(true) else setEmailError(false)

    }

    private val _passwordErrorMessage = MutableStateFlow<String?>(null)
    val passwordErrorMessage = _passwordErrorMessage.asStateFlow()

    protected fun setPasswordErrorMessage(message: String?) {
        _passwordErrorMessage.value = message
        if(!message.isNullOrBlank()) setPasswordError(true) else setPasswordError(false)
    }

    protected fun isEmailBlank(): Boolean {
        return _email.clip().isBlank()
    }

    protected fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(_email.clip()).matches()
    }


    protected fun isPasswordBlank(): Boolean {
        return _password.clip().isBlank()
    }

    protected fun isPasswordLengthValid(): Boolean {
        return _password.clip().length >= 8
    }

    protected fun isPasswordHasNumber(): Boolean {
        return Regex(".*\\d.*").matches(_password.clip())
    }

    protected fun isPasswordHasUpperCase(): Boolean {
        return Regex(".*[A-Z].*").matches(_password.clip())
    }

    protected fun isPasswordHasLowerCase(): Boolean {
        return Regex(".*[a-z].*").matches(_password.clip())
    }

    // Reset Values
    private fun resetEmailError() {
        setEmailErrorMessage(null)
    }

    private fun resetPasswordError() {
        setPasswordErrorMessage(null)
    }

    protected open fun resetErrors() {
        resetEmailError()
        resetPasswordError()
    }

    fun onSignInWithGoogle(
        credential: Credential,
        onSuccess: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                }
                Log.d("AuthViewModel", "Success")
                if (accountService.currentUser == null) {
                    for(i in 1..10) {
                        delay(500L)
                        if(accountService.currentUser != null) break
                        if(i == 10 && accountService.currentUser == null) {
                            val exceptionMessage = "Something went wrong, check your internet connection!"
                            application.toast(exceptionMessage)
                            throw Exception(exceptionMessage)
                        }
                    }
                }

                onSuccess(if(!appRepository.isUserRegistered(accountService.currentUserId)) REGISTER else null)

            } catch (e: Exception) {
                Log.e("AuthViewModel", "onSignInWithGoogle: $e")
                appLoaded()
            }
        }
    }

}

sealed class FormValidationResult {
    data object Valid: FormValidationResult()

    sealed class SignInResult: FormValidationResult() {
        data class EmailError(val message: String): SignInResult()
        data class PasswordError(val message: String): SignInResult()
    }

    sealed class SignUpResult: FormValidationResult() {
        data class EmailError(val message: String): SignUpResult()
        data class PasswordError(val message: String): SignUpResult()
        data class ConfirmPasswordError(val message: String): SignUpResult()
    }

    sealed class RegisterResult: FormValidationResult() {
        data class FullNameError(val message: String): RegisterResult()
        data class NicknameError(val message: String): RegisterResult()
        data class UsernameError(val message: String): RegisterResult()
    }
}
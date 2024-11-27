package com.mightsana.goodminton.features.auth.sign_in

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.mightsana.goodminton.EMAIL_VERIFICATION
import com.mightsana.goodminton.R
import com.mightsana.goodminton.REGISTER
import com.mightsana.goodminton.features.auth.AuthViewModel
import com.mightsana.goodminton.features.auth.FormValidationResult
import com.mightsana.goodminton.model.ext.clip
import com.mightsana.goodminton.model.ext.toast
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    accountService: AccountService,
    appRepository: AppRepository,
    application: Application,
): AuthViewModel(accountService, appRepository, application) {
    fun validateSignInForm(
        onSuccess: () -> Unit,
    ) {
        appLoading()
        resetErrors()
        val validationState = when {
            isEmailBlank()-> FormValidationResult.SignInResult.EmailError(application.getString(R.string.email_blank))
            !isEmailValid() -> FormValidationResult.SignInResult.EmailError(application.getString(R.string.email_invalid))
            isPasswordBlank() -> FormValidationResult.SignInResult.PasswordError(application.getString(R.string.password_blank))
            else -> FormValidationResult.Valid
        }

        when(validationState) {
            is FormValidationResult.SignInResult.EmailError -> {
                setEmailErrorMessage(validationState.message)
                appLoaded()
            }
            is FormValidationResult.SignInResult.PasswordError -> {
                setPasswordErrorMessage(validationState.message)
                appLoaded()
            }
            else -> onSuccess()
        }
    }

    fun onSignInWithEmailAndPassword(
        onSuccess: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                appLoading()
                updateEmail(_email.clip())
                updatePassword(_password.clip())
                accountService.signInWithEmailAndPassword(
                    email = _email.value,
                    password = _password.value
                )

                if (!accountService.isEmailVerified())
                    onSuccess(EMAIL_VERIFICATION)
                else if (!appRepository.isUserRegistered(accountService.currentUserId))
                    onSuccess(REGISTER)
                else
                    onSuccess(null)
            } catch (e: FirebaseNetworkException) {
                application.toast(e.message!!)
            } catch (e: Exception) {
                Log.e("SignInViewModel", e.message.toString())
                application.toast(R.string.login_error)
            } finally {
                appLoaded()
            }
        }
    }
}


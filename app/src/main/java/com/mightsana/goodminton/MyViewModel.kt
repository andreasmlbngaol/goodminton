package com.mightsana.goodminton

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightsana.goodminton.model.ext.toast
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class MyViewModel(
    protected val accountService: AccountService,
    protected val appRepository: AppRepository,
    protected val application: Application
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun appLoading() {
        _isLoading.value = true
    }

    fun appLoaded() {
        _isLoading.value = false
    }

    protected fun launchCatching(
        exception: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e("OneViewModel", e.message.toString())
                exception(e)
            }
        }
    }

    fun onSignOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            accountService.signOut()
            onSuccess()
        }
    }

    protected fun openOtherApp(
        category: String,
        packageName: String,
        flags: Int = Intent.FLAG_ACTIVITY_NEW_TASK
    ) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(category)
            setPackage(packageName)
            addFlags(flags)
        }
        try {
            application.startActivity(intent)
        } catch (e: Exception) {
            Log.e("OneViewModel", e.toString())
            e.printStackTrace()
        }
    }

    fun toast(message: String) {
        application.toast(message)
    }

    fun toast(@StringRes messageStringRes: Int) {
        application.toast(messageStringRes)
    }

    fun comingSoon() {
        toast("Coming Soon 😊")
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }
}


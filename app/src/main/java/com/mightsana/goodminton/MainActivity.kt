package com.mightsana.goodminton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mightsana.goodminton.features.auth.model.AuthCheck
import com.mightsana.goodminton.features.auth.model.SignIn
import com.mightsana.goodminton.model.ext.ExitWithDoublePress
import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.service.AccountService
import com.mightsana.goodminton.model.values.SharedPreference
import com.mightsana.goodminton.ui.theme.AppTheme
import com.mightsana.goodminton.view.Loader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var accountService: AccountService
    @Inject lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences(SharedPreference.PREF_NAME, MODE_PRIVATE)
        val dynamicColor = sharedPreferences.getBoolean(SharedPreference.PREF_DYNAMIC_COLOR, false)
        val weatherTheme = sharedPreferences.getBoolean(SharedPreference.PREF_WEATHER_THEME, false)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var startDestination: Any? by remember { mutableStateOf(null) }
            var authStartDestination: Any by remember { mutableStateOf(SignIn) }

            AuthCheck(Main, accountService, appRepository) { navStart, authStart ->
                startDestination = navStart
                authStartDestination = authStart
            }

            AppTheme(weatherTheme, dynamicColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Loader(startDestination == null) {
                        ExitWithDoublePress()
                        startDestination?.let { startDestination ->
                            MyNavHost(navController, startDestination, authStartDestination)
                        }
                    }
                }
            }
        }
    }
}

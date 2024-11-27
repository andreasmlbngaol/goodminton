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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mightsana.goodminton.features.auth.model.AuthCheck
import com.mightsana.goodminton.features.auth.model.authGraph
import com.mightsana.goodminton.model.ext.ExitWithDoublePress
import com.mightsana.goodminton.model.service.AccountService
import com.mightsana.goodminton.ui.theme.AppTheme
import com.mightsana.goodminton.view.Loader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var accountService: AccountService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                var navStart: String? by remember { mutableStateOf(null) }
                var authStart by remember { mutableStateOf(SIGN_IN) }
                AuthCheck(MAIN, accountService) { nav, auth ->
                    navStart = nav
                    authStart = auth
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Loader(navStart == null) {
                        ExitWithDoublePress()
                        navStart?.let { start ->
                            NavHost(
                                navController = navController,
                                startDestination = start
                            ) {
                                authGraph(
                                    navController = navController,
                                    mainRoute = MAIN,
                                    defaultWebClientId = getString(R.string.default_web_client_id),
                                    startDestination = authStart
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


const val SIGN_IN = "SignIn"
const val SIGN_UP = "SignUp"
const val EMAIL_VERIFICATION = "EmailVerification"
const val REGISTER = "Register"
const val MAIN = "Main"
const val AUTH_GRAPH = "AuthGraph"
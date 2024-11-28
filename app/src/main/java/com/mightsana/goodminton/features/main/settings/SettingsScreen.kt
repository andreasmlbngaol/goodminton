package com.mightsana.goodminton.features.main.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState()

    BackHandler {
        onBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Switch(
                checked = isDynamicColorEnabled,
                onCheckedChange = {
                    viewModel.setDynamicColorEnabled(it)
                }
            )
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp),
                visible = viewModel.showSnackbar.collectAsState().value,
            ) {
                Snackbar(
                    action = {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors().copy(
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {
                                viewModel.restartApp()
                            }
                        ) {
                            Text("Restart")
                        }
                    },
                    dismissAction = {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors().copy(
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {
                                viewModel.setShowSnackbar(false)
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text("Restart app to apply changes.")
                }
            }
        }
    }
}
package com.mightsana.goodminton.features.main.settings

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.model.component_model.RequestLocationPermission
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    BackHandler { onBack() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = { MyIcon(MyIcons.Back) }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Size.padding),
                verticalArrangement = Arrangement.spacedBy(Size.smallPadding)
            ) {
                // Weather Theme
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weather Theme (Experimental)")
                    RequestLocationPermission(
                        askForPermissionContent = { askPermission ->
                            Button(onClick = askPermission) { Text("Location Needed") }
                        }
                    ) {
                        Switch(
                            checked = viewModel.weatherThemeEnabled.collectAsState().value,
                            onCheckedChange = { viewModel.setWeatherThemeEnabled(it) }
                        )
                    }
                }

                // Dynamic Color Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dynamic Color")
                    Switch(
                        checked = viewModel.dynamicColorEnabled.collectAsState().value,
                        onCheckedChange = { viewModel.setDynamicColorEnabled(it) },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    )
                }
            }
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
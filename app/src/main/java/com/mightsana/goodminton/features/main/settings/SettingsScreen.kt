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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.component_model.RequestLocationPermission
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenDrawer: suspend () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    BackHandler { onBack() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_label)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                onOpenDrawer()
                            }
                        },
                        content = { MyIcon(MyIcons.Menu) }
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
                    Text(stringResource(R.string.weather_theme_label))
                    RequestLocationPermission(
                        askForPermissionContent = { askPermission ->
                            Button(onClick = askPermission) { Text(stringResource(R.string.location_permission_label)) }
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
                    Text(stringResource(R.string.dynamic_color_label))
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
                    .padding(bottom = Size.padding)
                    .padding(horizontal = Size.padding),
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
                            Text(stringResource(R.string.restart))
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
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                ) {
                    Text(stringResource(R.string.restart_description))
                }
            }
        }
    }
}
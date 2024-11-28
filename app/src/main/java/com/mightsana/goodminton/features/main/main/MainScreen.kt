package com.mightsana.goodminton.features.main.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.home.HomeScreen
import com.mightsana.goodminton.features.main.model.NavigationItem
import com.mightsana.goodminton.features.main.settings.SettingsScreen
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import kotlinx.coroutines.launch

const val HOME = "Home"
const val NOTIFICATIONS = "Notifications"
const val SETTINGS = "Settings"

@Composable
fun MainScreen(
    appNavController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainNavController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigationGroups = listOf(
        listOf(
            NavigationItem(
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                label = HOME,
                route = HOME,
                content = {
                    HomeScreen(
                        drawerState = drawerState,
                        appNavController = appNavController
                    )
                }
            ),
            NavigationItem(
                iconSelected = Icons.Filled.Notifications,
                iconUnselected = Icons.Outlined.Notifications,
                label = NOTIFICATIONS,
                route = NOTIFICATIONS,
                badgeCount = viewModel.friendRequestReceivedCount.collectAsState().value
            )
        ),

        listOf(
            NavigationItem(
                iconSelected = Icons.Filled.Settings,
                iconUnselected = Icons.Outlined.Settings,
                label = SETTINGS,
                route = SETTINGS,
                content = {
                    SettingsScreen(
                        onBack = {
                            viewModel.onSelectItem(HOME)
                            mainNavController.navigateAndPopUp(HOME, SETTINGS)
                        }
                    )
                }
            )
        )
    )

    val selectedNavigationItem by viewModel.selectedItem.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState,
                drawerShape = RectangleShape
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    navigationGroups.forEach { group ->
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        group.forEach{ item ->
                            val selected = selectedNavigationItem == item.route
                            NavigationDrawerItem(
                                badge = item.badgeCount?.let {
                                    {
                                        Text(item.badgeCount.toString())
                                    }
                                },
                                icon = {
                                    Icon(
                                        if (selected) item.iconSelected else item.iconUnselected,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        viewModel.onSelectItem(item.route)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = mainNavController,
            startDestination = selectedNavigationItem
        ) {
            navigationGroups.forEach { group ->
                group.forEach { item ->
                    composable(item.route) {
                        item.content()
                    }
                }
            }
        }
    }
}
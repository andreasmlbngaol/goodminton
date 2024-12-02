package com.mightsana.goodminton.features.main.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.home.HomeScreen
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.features.main.settings.SettingsScreen
import com.mightsana.goodminton.features.main.social.SocialScreen
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch

const val HOME = "Home"
const val NOTIFICATIONS = "Notifications"
const val SETTINGS = "Settings"

@Composable
fun MainContainer(
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
                iconSelected = MyIcons.SocialSelected,
                iconUnselected = MyIcons.SocialUnselected,
                label = "Social",
                route = "Social",
                badgeCount = viewModel.friendRequestSent.collectAsState().value.size,
                content = {
                    SocialScreen(
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
                badgeCount = viewModel.friendRequestReceived.collectAsState().value.size
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
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .widthIn(max = 300.dp)
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                    ) {
                        MyImage(painterResource(
                            R.drawable.ic_launcher_foreground),
                            modifier = Modifier.height(100.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
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
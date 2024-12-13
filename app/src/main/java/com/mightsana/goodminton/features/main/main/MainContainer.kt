package com.mightsana.goodminton.features.main.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import com.mightsana.goodminton.League
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.home.HomeScreen
import com.mightsana.goodminton.features.main.notifications.NotificationsScreen
import com.mightsana.goodminton.features.main.settings.SettingsScreen
import com.mightsana.goodminton.features.main.social.SocialScreen
import com.mightsana.goodminton.features.profile.model.Profile
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.model.ext.navigateAndPopUpTo
import com.mightsana.goodminton.model.ext.navigateSingleTop
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable object Home
@Serializable object Notifications
@Serializable object Social
@Serializable object Settings

@Composable
fun MainContainer(
    navController: NavHostController,
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
                label = stringResource(R.string.home_label),
                route = Home
            ),
            NavigationItem(
                iconSelected = MyIcons.SocialSelected,
                iconUnselected = MyIcons.SocialUnselected,
                label = stringResource(R.string.social_label),
                route = Social,
                badgeCount = viewModel.friendRequestReceived.collectAsState().value.size
            ),
            NavigationItem(
                iconSelected = Icons.Filled.Notifications,
                iconUnselected = Icons.Outlined.Notifications,
                label = stringResource(R.string.notifications_label),
                route = Notifications,
                badgeCount = viewModel.invitationReceived.collectAsState().value.size
            )
        ),

        listOf(
            NavigationItem(
                iconSelected = Icons.Filled.Settings,
                iconUnselected = Icons.Outlined.Settings,
                label = stringResource(R.string.settings_label),
                route = Settings
            )
        )
    )

    val selectedNavigationItem by viewModel.selectedItem.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .widthIn(max = 300.dp)
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                    ) {
                        MyImage(painterResource(
                            R.drawable.ic_launcher_round),
                            modifier = Modifier.height(70.dp)
                        )
                        Spacer(modifier = Modifier.width(Size.padding))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    navigationGroups.forEach { group ->
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(Size.smallPadding))
                        group.forEach{ item ->
                            val selected = selectedNavigationItem == item.route
                            NavigationDrawerItem(
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                badge = item.badgeCount?.let {
                                    { Text(item.badgeCount.toString()) }
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
                                        val currentRoute = selectedNavigationItem
                                        viewModel.onSelectItem(item.route)
                                        if (item.route != currentRoute)
                                            mainNavController.navigateAndPopUpTo(item.route, currentRoute)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(Size.smallPadding))
                        }
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = mainNavController,
            startDestination = Home
        ) {
            fun backToHome(currentRoute: Any) {
                viewModel.onSelectItem(Home)
                mainNavController.navigateAndPopUpTo(Home, currentRoute)
            }

            composable<Home> {
                HomeScreen(
                    onNavigateToProfile = { navController.navigateSingleTop(Profile()) },
                    onNavigateToLeague = { navController.navigateSingleTop(League(it)) },
                    onOpenDrawer = { drawerState.open() }
                )
            }

            composable<Social> {
                SocialScreen(
                    onBack = { backToHome(Social) },
                    onNavigateToProfile = { navController.navigateSingleTop(Profile()) },
                    onNavigateToOtherProfile = { navController.navigateSingleTop(Profile(it))},
                    onOpenDrawer = { drawerState.open() }
                )
            }

            composable<Notifications> {
                NotificationsScreen(
                    onBack = { backToHome(Notifications) },
                    onOpenDrawer = { drawerState.open() },
                    onNavigateToLeague = { navController.navigateSingleTop(League(it)) }
                )
            }

            composable<Settings> {
                SettingsScreen(
                    onBack = { backToHome(Settings) },
                    onOpenDrawer = { drawerState.open() }
                )
            }
        }
    }
}
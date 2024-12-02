package com.mightsana.goodminton.features.main.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.features.main.detail.info.LeagueInfoScreen
import com.mightsana.goodminton.features.main.detail.participants.ParticipantsScreen
import com.mightsana.goodminton.features.main.detail.standings.StandingsScreen
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContainer(
    leagueId: String,
    appNavController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.observeLeague(leagueId)
    }
    val user by viewModel.user.collectAsState()
    val leagueInfo by viewModel.leagueInfo.collectAsState()
    val participantsUI by viewModel.leagueParticipantsUI.collectAsState()

    val navItems = listOf(
        NavigationItem(
            label = MATCH,
            route = MATCH,
            iconSelected = Icons.Filled.Schedule,
            iconUnselected = Icons.Outlined.Schedule
        ),
        NavigationItem(
            label = STANDINGS,
            route = STANDINGS,
            iconSelected = Icons.Filled.Leaderboard,
            iconUnselected = Icons.Outlined.Leaderboard,
            content = {
                StandingsScreen(viewModel = viewModel)
            }
        ),
        NavigationItem(
            label = PARTICIPANTS,
            route = PARTICIPANTS,
            iconSelected = Icons.Filled.People,
            iconUnselected = Icons.Outlined.People,
            fab = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {}
                    ) {
                        MyIcon(MyIcons.Anonymous)
                    }
                    ExtendedFloatingActionButton(
                        text = {
                            Text("Add Participant")
                        },
                        icon = {
                            MyIcon(MyIcons.Plus)
                        },
                        onClick = {}
                    )
                }
            },
            content = {
                ParticipantsScreen(viewModel = viewModel)
            }
        ),
        NavigationItem(
            label = INFO,
            route = INFO,
            iconSelected = Icons.Filled.Info,
            iconUnselected = Icons.Outlined.Info,
            content = {
                LeagueInfoScreen(viewModel = viewModel)
            }
        )
    )
    val selectedItem by viewModel.selectedItem.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(leagueInfo.name) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            appNavController.popBackStack()
                        }
                    ) {
                        MyIcon(MyIcons.Back)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    val selected = index == selectedItem
                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                if(selected) item.iconSelected else item.iconUnselected,
                                contentDescription = null
                            )
                        },
                        label = { Text(item.label) },
                        onClick = { viewModel.onSelectItem(index) }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedContent(selectedItem, label = "") { selected ->
                navItems[selected].fab?.let { fab ->
                    if (participantsUI.find { it.user.uid == user.uid }?.info?.role == Role.Creator) {
                        fab()
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            navItems[selectedItem].content()
        }
    }
}

const val MATCH = "Match"
const val STANDINGS = "Standings"
const val PARTICIPANTS = "Participants"
const val INFO = "Info"

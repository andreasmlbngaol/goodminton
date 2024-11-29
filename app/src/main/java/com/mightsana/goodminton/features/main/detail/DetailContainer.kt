package com.mightsana.goodminton.features.main.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.features.main.detail.participants.ParticipantsScreen
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.view.MyIcon

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
    val leagueInfo by viewModel.leagueInfo.collectAsState()
    val leagueParticipants by viewModel.leagueParticipants.collectAsState()
    val participationInfo by viewModel.participationInfo.collectAsState()

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
            iconUnselected = Icons.Outlined.Leaderboard
        ),
        NavigationItem(
            label = PARTICIPANTS,
            route = PARTICIPANTS,
            iconSelected = Icons.Filled.People,
            iconUnselected = Icons.Outlined.People,
            content = {
                ParticipantsScreen(
                    participantUsers = leagueParticipants,
                    participantInfos = participationInfo,
                    viewModel = viewModel
                )
            }
        ),
        NavigationItem(
            label = INFO,
            route = INFO,
            iconSelected = Icons.Filled.Info,
            iconUnselected = Icons.Outlined.Info
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
                        MyIcon(Icons.AutoMirrored.Filled.ArrowBack)
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

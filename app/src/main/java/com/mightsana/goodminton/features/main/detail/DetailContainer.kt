package com.mightsana.goodminton.features.main.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.features.main.detail.info.LeagueInfoScreen
import com.mightsana.goodminton.features.main.detail.matches.MatchesScreen
import com.mightsana.goodminton.features.main.detail.participants.ParticipantsScreen
import com.mightsana.goodminton.features.main.detail.standings.StandingsScreen
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.PullToRefreshScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContainer(
    leagueId: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.observeLeague(leagueId) }
    val user by viewModel.user.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val invitationSent by viewModel.invitationSent.collectAsState()
    val leagueJoint by viewModel.leagueJoint.collectAsState()
    val participants by viewModel.leagueParticipantsJoint.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        NavigationItem(
            label = MATCH,
            route = MATCH,
            iconSelected = Icons.Filled.Schedule,
            iconUnselected = Icons.Outlined.Schedule,
            content = {
                MatchesScreen(viewModel = viewModel)
            }
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
                if (participants.find { it.user.uid == user.uid }?.role == Role.Creator) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        FloatingActionButton(
                            onClick = {}
                        ) {
                            MyIcon(MyIcons.Plus)
                        }
                        ExtendedFloatingActionButton(
                            text = {
                                Text("Invite Friend")
                            },
                            icon = {
                                MyIcon(MyIcons.Invitation)
                            },
                            onClick = {
                                scope.launch {
                                    viewModel.showParticipantsSheet()
                                    sheetState.show()
                                }
                            }
                        )
                    }
                } else {
                    Text("For Participant Only")
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
                LeagueInfoScreen(
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
        )
    )
    val selectedItem by viewModel.selectedItem.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(leagueJoint.name) },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
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
                        fab()
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                PullToRefreshScreen( { viewModel.observeLeague(leagueId) } ) {
                    navItems[selectedItem].content()
                }
            }
        }

        AnimatedVisibility(viewModel.participantsSheetExpanded.collectAsState().value) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.dismissParticipantsSheet()
                    }
                },
                sheetState = sheetState
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val invitationReceiverIds = invitationSent.map { it.receiver.uid }
                    val friendsAvailable = friends
                        .filter { it.user.uid !in invitationReceiverIds }
                    items(friendsAvailable) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                MyImage(
                                    it.user.profilePhotoUrl,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(40.dp)
                                )
                            },
                            headlineContent = { Text(it.user.name) },
                            supportingContent = { Text(it.user.username) },
                            trailingContent = {
                                Button(
                                    onClick = {}
                                ) {
                                    Text("Invite")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

const val MATCH = "Match"
const val STANDINGS = "Standings"
const val PARTICIPANTS = "Participants"
const val INFO = "Info"

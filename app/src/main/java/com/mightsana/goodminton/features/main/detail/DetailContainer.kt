package com.mightsana.goodminton.features.main.detail

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.detail.info.LeagueInfoScreen
import com.mightsana.goodminton.features.main.detail.matches.MatchesScreen
import com.mightsana.goodminton.features.main.detail.participants.ParticipantsScreen
import com.mightsana.goodminton.features.main.detail.standings.StandingsScreen
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.model.values.Size
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
    val matches by viewModel.matches.collectAsState()
    val league by viewModel.leagueJoint.collectAsState()
    val participants by viewModel.leagueParticipantsJoint.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val playerDropdownExpanded by viewModel.matchPlayersExpanded.collectAsState()
    val playerSelected by viewModel.playerSelected.collectAsState()
    val scope = rememberCoroutineScope()
    val playerPerMatch = if(league.double) 4 else 2
    val notParticipant = user.uid !in participants.map { it.user.uid }

    val navItems = listOf(
        NavigationItem(
            label = MATCH,
            route = MATCH,
            iconSelected = Icons.Filled.Schedule,
            iconUnselected = Icons.Outlined.Schedule,
            content = {
                MatchesScreen(
                    onNavigateToParticipant = {
                        viewModel.onSelectItem(2)
                        viewModel.resetPlayerSelected()
                    },
                    viewModel = viewModel
                )
            },
            fab = {
                val role = participants.find { it.user.uid == user.uid }?.role
                if ((role == Role.Creator || role == Role.Admin) && league.status != LeagueStatus.Finished) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if(participants.size >= playerPerMatch) {
                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.showMatchSheet()
                                        sheetState.show()
                                    }
                                }
                            ) {
                                MyIcon(MyIcons.Plus)
                            }
                            val anyNotFinished =
                                matches.any { it.status == MatchStatus.Scheduled || it.status == MatchStatus.Playing }
                            val containerColor =
                                if (anyNotFinished) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primary
                            val contentColor =
                                contentColorFor(containerColor).copy(if (anyNotFinished) 0.38f else 1f)
                            ExtendedFloatingActionButton(
                                text = {
                                    Text("Auto Generate")
                                },
                                icon = {
                                    MyIcon(MyIcons.Generate)
                                },
                                containerColor = containerColor,
                                contentColor = contentColor,
                                expanded = !anyNotFinished,
                                onClick = {
                                    if (anyNotFinished) {
                                        viewModel.toast(R.string.error_generate_match)
                                    } else {
                                        viewModel.autoGenerateMatch()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ),
        NavigationItem(
            label = STANDINGS,
            route = STANDINGS,
            iconSelected = Icons.Filled.Leaderboard,
            iconUnselected = Icons.Outlined.Leaderboard,
            content = { StandingsScreen(viewModel = viewModel) }
        ),
        NavigationItem(
            label = PARTICIPANTS,
            route = PARTICIPANTS,
            iconSelected = Icons.Filled.People,
            iconUnselected = Icons.Outlined.People,
            content = { ParticipantsScreen(viewModel = viewModel) },
            fab = {
                val myRole = participants.find { it.user.uid == user.uid }?.role
                if ((myRole == Role.Creator || myRole == Role.Admin) && league.status != LeagueStatus.Finished) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.comingSoon()
                            }
                        ) {
                            MyIcon(MyIcons.Plus)
                        }
                        ExtendedFloatingActionButton(
                            text = {
                                Text("Add Friend")
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
                }
            }
        ),
        NavigationItem(
            label = INFO,
            route = INFO,
            iconSelected = Icons.Filled.Info,
            iconUnselected = Icons.Outlined.Info,
            content = {
                LeagueInfoScreen(
                    viewModel,
                    onBack
                )
            }
        )
    )
    val selectedItem by viewModel.selectedItem.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Loader(viewModel.isLoading.collectAsState().value) {
        val userInvited = user.uid in invitationSent.map { it.receiver.uid }
        Scaffold(
            floatingActionButtonPosition = if(userInvited || (notParticipant))
                FabPosition.Center else FabPosition.End,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(league.name, overflow = Ellipsis, maxLines = 1) },
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
                NavigationBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ))
                ) {
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
                            onClick = {
                                viewModel.onSelectItem(index)
                                viewModel.resetPlayerSelected()
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                if(notParticipant) {
                    ExtendedFloatingActionButton(
                        text = { Text("Join") },
                        icon = { MyIcon(MyIcons.Join) },
                        onClick = { viewModel.showJoinDialog() }
                    )
                } else if(userInvited) {
                    val invitationId = invitationSent.find { it.receiver.uid == user.uid }!!.id
                    Row {
                        Button(
                            onClick = {
                                viewModel.acceptInvitation(
                                    invitationId = invitationId,
                                    leagueId = league.id
                                )
                            }
                        ) {
                            Text("Accept")
                        }
                        Spacer(Modifier.width(Size.padding))
                        OutlinedButton(
                            onClick = {
                                viewModel.declineInvitation(invitationId)
                            }
                        ) {
                            Text("Decline")
                        }
                    }
                } else {
                    AnimatedContent(selectedItem, label = "") { selected ->
                        navItems[selected].fab?.let { fab ->
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
                PullToRefreshScreen( { viewModel.observeLeague(leagueId) } ) {
                    navItems[selectedItem].content()
                }
            }
        }

        if(viewModel.participantsSheetExpanded.collectAsState().value) {
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
                    val participantIds = participants.map { it.user.uid }
                    val friendsAvailable = friends
                        .filter { it.user.uid !in participantIds }
                    if(friendsAvailable.isEmpty())
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Size.padding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No Friends Can Be Invited")
                            }
                        }
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
                                if(it.user.uid !in invitationReceiverIds) {
                                    if(it.user.openToAdd) {
                                        Button(onClick = { viewModel.addParticipant(it.user.uid) }) {
                                            Text("Add")
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.inviteFriend(it.user.uid) },
                                            colors = ButtonDefaults.buttonColors().copy(
                                                containerColor = MaterialTheme.colorScheme.tertiary,
                                                contentColor = MaterialTheme.colorScheme.onTertiary,
                                                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                                disabledContentColor = MaterialTheme.colorScheme.onTertiary
                                            )
                                        ) {
                                            Text("Invite")
                                        }
                                    }
                                } else {
                                    val invitationId = invitationSent.find { inv -> inv.receiver.uid == it.user.uid }!!.id
                                    TextButton(onClick = { viewModel.cancelInvitation(invitationId) }) {
                                        Text("Invited")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        if(viewModel.matchSheetExpanded.collectAsState().value) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.dismissMatchSheet()
                    }
                },
                sheetState = sheetState
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth().
                        padding(Size.padding),
                    contentAlignment = Alignment.Center,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 500.dp),
                        verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val playerCount = if(league.double) 4 else 2
                        itemsIndexed((1..playerCount).toList()) { index, item ->
                            MatchDropdownButton(
                                dropdownExpandedMap = playerDropdownExpanded,
                                order = item,
                                selectedItemMap = playerSelected,
                                items = participants.sortedBy { it.user.name },
                                onToggle = { viewModel.togglePlayerExpanded(it) },
                                onDismiss = { viewModel.dismissPlayerExpanded(it) },
                                onSelected = { selectedParticipantId -> viewModel.selectPlayer(item, selectedParticipantId) }
                            )
                            if((index + 1) == (playerCount / 2)) {
                                Text(
                                    text = "VS",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = Bold,
                                    modifier = Modifier.padding(top = Size.smallPadding)
                                )
                            }
                        }

                        item {
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.resetPlayerSelected()
                                    }
                                ) {
                                    Text("Reset")
                                }
                                Spacer(Modifier.width(Size.padding))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            viewModel.createMatch()
                                            sheetState.hide()
                                            viewModel.resetPlayerSelected()
                                            viewModel.dismissMatchSheet()
                                        }
                                    }
                                ) {
                                    Text("Create")
                                }
                            }
                        }
                    }
                }
            }
        }
        if(viewModel.joinDialogVisible.collectAsState().value) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissJoinDialog() },
                confirmButton = {
                    Button(onClick = { viewModel.joinLeague() }) {
                        Text("Join")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { viewModel.dismissJoinDialog() }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Join League") },
                text = { Text("Are you sure you want to join this league?") }
            )
        }
    }
}

@Composable
fun MatchDropdownButton(
    dropdownExpandedMap: Map<Int, Boolean>,
    order: Int,
    selectedItemMap: Map<Int, String?>,
    items: List<LeagueParticipantJoint>,
    onToggle: (Int) -> Unit,
    onDismiss: (Int) -> Unit,
    selectLabel: String = "Select Player $order",
    changeLabel: String = "Change",
    onSelected: (String?) -> Unit
) {
    val selectedItem = items.find {
        Log.d("DetailContainer", "user.uid: ${it.user.uid}")
        Log.d("DetailContainer", "selectedItemId: ${selectedItemMap[order]}")
        it.id == selectedItemMap[order]
    }
    AnimatedContent(selectedItem, label = "") { selected ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(Size.smallPadding),
            verticalAlignment = CenterVertically,
        ) {
            selected?.let {
                MyImage(
                    it.user.profilePhotoUrl,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                )
                Text(
                    it.user.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onSelected(null) }) {
                    MyIcon(MyIcons.Cancel)
                }
            }
            val itemsLeft = items
                .filter { item -> item.id !in selectedItemMap.values }
            if(itemsLeft.isNotEmpty()) {
                Box {
                    val isDropdownExpanded = dropdownExpandedMap[order] == true
                    OutlinedButton(onClick = { onToggle(order) }) {
                        Text(if (selected == null) selectLabel else changeLabel)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { onDismiss(order) }
                    ) {
                        itemsLeft.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.padding(vertical = Size.extraSmallPadding),
                                        horizontalArrangement = Arrangement.spacedBy(Size.smallPadding),
                                        verticalAlignment = CenterVertically
                                    ) {
                                        if (item.user.name != UNSELECT)
                                            MyImage(
                                                item.user.profilePhotoUrl,
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .size(40.dp)
                                            )
                                        Text(
                                            item.user.name,
                                            maxLines = 1,
                                            style = MaterialTheme.typography.titleSmall,
                                            overflow = Ellipsis,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = if (item.user.name == UNSELECT) TextAlign.Center else TextAlign.Start
                                        )
                                    }
                                },
                                onClick = {
                                    onSelected(item.id.ifEmpty { null })
                                    onDismiss(order)
                                }
                            )
                        }
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
const val UNSELECT = "Unselect"
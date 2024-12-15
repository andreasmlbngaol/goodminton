package com.mightsana.goodminton.features.main.detail.participants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.InvitationJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyImage

@Composable
@Suppress("unused")
fun ParticipantsScreen(viewModel: DetailViewModel) {
    val uid = viewModel.user.collectAsState().value.uid
    val league by viewModel.leagueJoint.collectAsState()
    val participantJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val currentUserRole = participantJoint.find { it.user.uid == uid }?.role
    val roleDropdownExpanded by viewModel.participantsRoleExpanded.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            participantJoint
                .sortedWith(
                    compareBy(
                        { it.role.ordinal },
                        { it.user.name }
                    )
                )
                .forEachIndexed { _, participant ->
                    val isDropdownExpanded = roleDropdownExpanded[participant.user.uid] == true
                    ListItem(
                        leadingContent = {
                            MyImage(
                                model = participant.user.profilePhotoUrl,
                                modifier = Modifier.width(40.dp).clip(CircleShape).aspectRatio(1f)
                            )
                        },
                        headlineContent = {
                            Text(
                                text = participant.user.name,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleMedium,
                                overflow = Ellipsis
                            )
                        },
                        supportingContent = {
                            Text(
                                text = participant.user.username,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall,
                                overflow = Ellipsis
                            )
                        },
                        trailingContent = {
                            if (currentUserRole == Role.Creator && participant.role != Role.Creator && !participant.user.uid.contains("GUEST") && league.status != LeagueStatus.Finished) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleParticipantsRoleExpanded(participant.user.uid) },
                                    contentPadding = PaddingValues(start = Size.padding, end = Size.smallPadding),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Size.smallPadding)
                                    ) {
                                        Text(participant.role.name)
                                        MyIcon(if (isDropdownExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown)
                                    }
                                }
                                DropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = {
                                        viewModel.dismissParticipantsRoleExpanded(
                                            participant.user.uid
                                        )
                                    }
                                ) {
                                    Role.entries.forEach { role ->
                                        if (role == Role.Creator) return@forEach
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    role.name,
                                                    maxLines = 1,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    overflow = Ellipsis
                                                )
                                            },
                                            onClick = {
                                                viewModel.changeParticipantRole(
                                                    leagueId = participant.league.id,
                                                    userId = participant.user.uid,
                                                    newRole = role.name
                                                )
                                                viewModel.dismissParticipantsRoleExpanded(participant.user.uid)
                                            },
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    participant.role.name,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleMedium,
                                    overflow = Ellipsis
                                )
                            }
                        }
                    )
                }
        }
        item { Spacer(Modifier.height(150.dp)) }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddParticipantSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    friends: List<FriendJoint>,
    participants: List<LeagueParticipantJoint>,
    invitationSent: List<InvitationJoint>,
    invitationId: String?,
    onAddFriend: (uid: String) -> Unit,
    onInviteFriend: (uid: String) -> Unit,
    onCancelInvitation: (id: String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            val invitationReceiverIds = invitationSent.map { it.receiver.uid }
            val participantIds = participants.map { it.user.uid }
            val friendsAvailable = friends.filter { it.user.uid !in participantIds }
            if(friendsAvailable.isEmpty())
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Size.padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.no_available_friends))
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
                                Button({ onAddFriend(it.user.uid) }) {
                                    Text(stringResource(R.string.add))
                                }
                            } else {
                                Button(
                                    onClick = { onInviteFriend(it.user.uid) },
                                    colors = ButtonDefaults.buttonColors().copy(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary,
                                        disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                        disabledContentColor = MaterialTheme.colorScheme.onTertiary
                                    )
                                ) {
                                    Text(stringResource(R.string.invite))
                                }
                            }
                        } else {
                            TextButton({ onCancelInvitation(invitationId!!) }) {
                                Text(stringResource(R.string.invited))
                            }
                        }
                    }
                )
            }
        }
    }
}

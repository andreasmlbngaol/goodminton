package com.mightsana.goodminton.features.main.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.component_model.NavigationItem
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons

@Composable
fun DetailFab(
    notParticipant: Boolean,
    leagueFinished: Boolean,
    userInvited: Boolean,
    selectedItem: Int,
    navItems: List<NavigationItem>,
    invitationId: String?,
    leagueId: String,
    onShowJoinDialog: () -> Unit,
    onAcceptInvitation: (invitationId: String, leagueId: String) -> Unit,
    onDeclineInvitation: (invitationId: String) -> Unit,
) {
    if(notParticipant && !leagueFinished) {
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.join)) },
            icon = { MyIcon(MyIcons.Join) },
            onClick = onShowJoinDialog
        )
    } else if(userInvited && !leagueFinished) {
        Row(horizontalArrangement = Arrangement.spacedBy(Size.padding)) {
            Button({ onAcceptInvitation(invitationId!!, leagueId) }) {
                Text(stringResource(R.string.accept_button_label))
            }
            OutlinedButton({ onDeclineInvitation(invitationId!!) }) {
                Text(stringResource(R.string.decline_button_label))
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

@Composable
fun MatchesFab(
    anyNotFinished: Boolean,
    onAddMatch: () -> Unit,
    onAutoGenerateMatch: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(onAddMatch) { MyIcon(MyIcons.Plus) }

        val containerColor = if (anyNotFinished) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primary
        val contentColor = contentColorFor(containerColor).copy(if (anyNotFinished) 0.38f else 1f)

        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.generate_match)) },
            icon = { MyIcon(MyIcons.Generate) },
            containerColor = containerColor,
            contentColor = contentColor,
            expanded = !anyNotFinished,
            onClick = onAutoGenerateMatch
        )
    }
}

@Composable
fun ParticipantsFab(
    onAddGuest: () -> Unit,
    onAddFriend: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton( onAddGuest) { MyIcon(MyIcons.Plus) }
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.add_friends)) },
            icon = { MyIcon(MyIcons.Invitation) },
            onClick = onAddFriend
        )
    }
}


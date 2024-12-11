package com.mightsana.goodminton.features.main.detail.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.ext.showDateTime
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyTextField

@Composable
fun LeagueInfoScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val uid = viewModel.user.collectAsState().value.uid
    val participantsJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val currentUserRole = participantsJoint.find { it.user.uid == uid }?.role
    val leagueJoint by viewModel.leagueJoint.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // League Name
        InfoItem(
            title = stringResource(R.string.league_name_label),
            value = leagueJoint.name,
            icon = {
                if (currentUserRole == Role.Creator) {
                    MyIcon(
                        MyIcons.Edit,
                        modifier = Modifier.onTap { viewModel.showChangeNameDialog() }
                    )
                } else {
                    MyIcon(
                        MyIcons.Copy,
                        modifier = Modifier.onTap {
                            clipboardManager.setText(AnnotatedString(leagueJoint.name))
                        }
                    )
                }
            }
        )

        // Participants
        InfoItem(
            title = stringResource(R.string.participants),
            value = participantsJoint.size.toString(),
        )

        // Discipline
        InfoItem(
            title = stringResource(R.string.discipline),
            value = if(leagueJoint.double) stringResource(R.string.double_label) else stringResource(R.string.single_label),
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueDiscipline(!leagueJoint.double) }
                    )
                }
            } else null
        )

        // Double Fixed
        if(leagueJoint.fixedDouble != null) {
            InfoItem(
                title = stringResource(R.string.fixed_double),
                value = if(leagueJoint.fixedDouble!!) stringResource(R.string.yes) else stringResource(R.string.no),
                icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                    {
                        MyIcon(
                            MyIcons.Flip,
                            modifier = Modifier.onTap {
                                viewModel.updateLeagueFixedDouble(!leagueJoint.fixedDouble!!)
                            }
                        )
                    }
                } else null
            )
        }

        // League Match Points
        InfoItem(
            title = stringResource(R.string.match_points_label),
            value = leagueJoint.matchPoints.toString(),
            icon = if (currentUserRole == Role.Creator) {
                {
                    MyIcon(
                        MyIcons.Edit,
                        modifier = Modifier.onTap {
                            viewModel.showChangeMatchPointsDialog()
                        }
                    )
                }
            } else null
        )

        // Deuce
        InfoItem(
            title = stringResource(R.string.deuce),
            value = if(leagueJoint.deuceEnabled) stringResource(R.string.yes) else stringResource(R.string.no),
        icon = if(currentUserRole == Role.Creator) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueDeuce(!leagueJoint.deuceEnabled) }
                    )
                }
            } else null
        )

        // Visibility
        InfoItem(
            title = stringResource(R.string.visibility_label),
            value = if(leagueJoint.private) stringResource(R.string.private_league_label) else stringResource(R.string.public_text),
            icon = if(currentUserRole == Role.Creator) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueVisibility(!leagueJoint.private) }
                    )
                }
            } else null
        )

        //League ID
        InfoItem(
            title = stringResource(R.string.league_id_label),
            value = leagueJoint.id,
            icon = {
                MyIcon(
                    MyIcons.Copy,
                    modifier = Modifier.onTap {
                        clipboardManager.setText(AnnotatedString(leagueJoint.id))
                    }
                )
            }
        )

        // Created At
        InfoItem(
            title = stringResource(R.string.created_at_label),
            value = leagueJoint.createdAt.showDateTime()
        )

        // Created By
        InfoItem(
            title = stringResource(R.string.created_by_label),
            value = leagueJoint.createdBy.name
        )


        // Delete Button & End League
        if(currentUserRole == Role.Creator) {
            val containerColor = MaterialTheme.colorScheme.errorContainer
            Row {
                if(!viewModel.matches.collectAsState().value.any { match ->
                    match.status == MatchStatus.Scheduled || match.status == MatchStatus.Playing
                }) {
                    Button(
                        onClick = { viewModel.showEndLeagueDialog() }
                    ) {
                        Text(stringResource(R.string.end_league))
                    }
                }
                Spacer(Modifier.width(Size.padding))
                Button(
                    onClick = { viewModel.showDeleteLeagueDialog() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = containerColor,
                        contentColor = contentColorFor(containerColor)
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }

    // End League Dialog
    if(viewModel.endLeagueDialogVisible.collectAsState().value) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEndLeagueDialog() },
            confirmButton = {
                Button(onClick = { viewModel.finishLeague() }) {
                    Text(stringResource(R.string.end))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissEndLeagueDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.end_league)) },
            text = { Text(stringResource(R.string.end_league_description)) }
        )
    }

    // League Name Dialog
    if(viewModel.changeNameDialogVisible.collectAsState().value) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissChangeNameDialog() },
            confirmButton = {
                Button(onClick = { viewModel.updateLeagueName() }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissChangeNameDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.league_name_label)) },
            text = {
                MyTextField(
                    value = viewModel.leagueName.collectAsState().value,
                    onValueChange = { viewModel.changeLeagueName(it) },
                    placeholder = { Text(leagueJoint.name) }
                )
            }
        )
    }

    // Match Points Dialog
    if(viewModel.changeMatchPointsDialogVisible.collectAsState().value) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissChangeMatchPointsDialog() },
            confirmButton = {
                Button(onClick = { viewModel.updateLeagueMatchPoints() }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissChangeMatchPointsDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.match_points_label)) },
            text = {
                val matchPoints by viewModel.matchPoints.collectAsState()
                MyTextField(
                    value = if(matchPoints != 0) matchPoints.toString() else "",
                    onValueChange = { viewModel.changeMatchPoints(it) },
                    placeholder = { Text(leagueJoint.matchPoints.toString()) }
                )
            }
        )
    }

    // Delete League
    if(viewModel.deleteLeagueDialogVisible.collectAsState().value) {
        val containerColor = MaterialTheme.colorScheme.errorContainer
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteLeagueDialog() },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteLeague(onBack) },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = containerColor,
                        contentColor = contentColorFor(containerColor)
                    )
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteLeagueDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.delete_league)) },
            text = { Text(stringResource(R.string.delete_league_description)) }
        )
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    //League ID
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End
                )
                icon?.let { icon() }
            }
        }
    )
}
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.features.main.result.generateAndSaveReportToMediaStore
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.ext.showDateTime
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyTextField

@Composable
@Suppress("unused")
fun LeagueInfoScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uid = viewModel.user.collectAsState().value.uid
    val participantsJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val participantsStats by viewModel.participantsStats.collectAsState()
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
            title = "League Name",
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
            title = "Participants",
            value = participantsJoint.size.toString(),
        )

        // Discipline
        InfoItem(
            title = "Discipline",
            value = if(leagueJoint.double) "Double" else "Single",
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
                title = "Fixed Double",
                value = if(leagueJoint.fixedDouble!!) "Yes" else "No",
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
            title = "Match Points",
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
            title = "Deuce",
            value = if(leagueJoint.deuceEnabled) "Yes" else "No",
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
            title = "Visibility",
            value = if(leagueJoint.private) "Private" else "Public",
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
            title = "ID",
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
            title = "Time Created",
            value = leagueJoint.createdAt.showDateTime()
        )

        // Created By
        InfoItem(
            title = "Creator",
            value = leagueJoint.createdBy.name
        )


        // Delete Button & Download Result
        if(leagueJoint.status == LeagueStatus.Finished) {
            Button(
                onClick = {
                    generateAndSaveReportToMediaStore(
                        context = context,
                        fileName = "${leagueJoint.name}_report",
                        league = leagueJoint,
                        participants = participantsJoint,
                        participantsStats = participantsStats,
                        matches = matches
                    )
                }
            ) {
                Text("Download Result")
            }
        } else if(currentUserRole == Role.Creator) {
            val containerColor = MaterialTheme.colorScheme.errorContainer
            Row {
                if(!viewModel.matches.collectAsState().value.any { match ->
                    match.status == MatchStatus.Scheduled || match.status == MatchStatus.Playing
                }) {
                    Button(
                        onClick = { viewModel.showEndLeagueDialog() }
                    ) {
                        Text("End League")
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
                    Text("Delete")
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
                    Text("End")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissEndLeagueDialog() }) {
                    Text("Cancel")
                }
            },
            title = { Text("End League") },
            text = { Text("Are you sure you want to end this league?") }
        )
    }

    // League Name Dialog
    if(viewModel.changeNameDialogVisible.collectAsState().value) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissChangeNameDialog() },
            confirmButton = {
                Button(onClick = { viewModel.updateLeagueName() }) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissChangeNameDialog() }) {
                    Text("Cancel")
                }
            },
            title = { Text("League Name") },
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
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissChangeMatchPointsDialog() }) {
                    Text("Cancel")
                }
            },
            title = { Text("Match Points") },
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
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteLeagueDialog() }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete League") },
            text = { Text("Are you sure you want to delete this league?") }
        )
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String,
    icon: @Composable (() -> Unit)? = null,
//    bottomLine: Boolean = true,
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
//    if(bottomLine)
//        HorizontalDivider()
}
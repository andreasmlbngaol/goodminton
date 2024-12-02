package com.mightsana.goodminton.features.main.detail.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.ext.showDateTime
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyTextField

@Composable
@Suppress("unused")
fun LeagueInfoScreen(
    viewModel: DetailViewModel,
    onNavigateToHome: () -> Unit
) {
    val uid = viewModel.user.collectAsState().value.uid
    val participantJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val currentUserRole = participantJoint.find { it.user.uid == uid }?.role
    val leagueJoint by viewModel.leagueJoint.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val changeNameDialogVisible by viewModel.changeNameDialogVisible.collectAsState()
    val changeMatchPointsDialogVisible by viewModel.changeMatchPointsDialogVisible.collectAsState()
    val deleteLeagueDialogVisible by viewModel.deleteLeagueDialogVisible.collectAsState()

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
            icon = if (currentUserRole == Role.Creator && matches.isEmpty()) {
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
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
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
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
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


        // Delete Button
        if(currentUserRole == Role.Creator) {
            val containerColor = MaterialTheme.colorScheme.errorContainer
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

    // League Name Dialog
    AnimatedVisibility(changeNameDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissChangeNameDialog()
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateLeagueName() }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissChangeNameDialog()
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("League Name")
            },
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
    AnimatedVisibility(changeMatchPointsDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissChangeMatchPointsDialog()
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateLeagueMatchPoints()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissChangeMatchPointsDialog()
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Match Points")
            },
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
    AnimatedVisibility(deleteLeagueDialogVisible) {
        val containerColor = MaterialTheme.colorScheme.errorContainer
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteLeagueDialog() },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLeague {
                            onNavigateToHome()
                        }
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = containerColor,
                        contentColor = contentColorFor(containerColor)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDeleteLeagueDialog() }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Delete League")
            },
            text = {
                Text("Are you sure you want to delete this league?")
            }
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
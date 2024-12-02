package com.mightsana.goodminton.features.main.detail.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
    viewModel: DetailViewModel
) {
    val uid = viewModel.user.collectAsState().value.uid
    val participantsUI by viewModel.leagueParticipantsUI.collectAsState()
    val currentUserRole = participantsUI.find { it.user.uid == uid }?.info?.role
    val leagueInfo by viewModel.leagueInfo.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val changeNameDialogVisible by viewModel.changeNameDialogVisible.collectAsState()
    val changeMatchPointsDialogVisible by viewModel.changeMatchPointsDialogVisible.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        // League Name
        InfoItem(
            title = "League Name",
            value = leagueInfo.name,
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
                            clipboardManager.setText(AnnotatedString(leagueInfo.name))
                        }
                    )
                }
            }
        )

        // Discipline
        InfoItem(
            title = "Discipline",
            value = if(leagueInfo.double) "Double" else "Single",
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueDiscipline(!leagueInfo.double) }
                    )
                }
            } else null
        )

        // Double Fixed
        if(leagueInfo.fixedDouble != null) {
            InfoItem(
                title = "Fixed Double",
                value = if(leagueInfo.fixedDouble!!) "Yes" else "No",
                icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                    {
                        MyIcon(
                            MyIcons.Flip,
                            modifier = Modifier.onTap {
                                viewModel.updateLeagueFixedDouble(!leagueInfo.fixedDouble!!)
                            }
                        )
                    }
                } else null
            )
        }

        // League Match Points
        InfoItem(
            title = "Match Points",
            value = leagueInfo.matchPoints.toString(),
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
            value = if(leagueInfo.deuceEnabled) "Yes" else "No",
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueDeuce(!leagueInfo.deuceEnabled) }
                    )
                }
            } else null
        )

        // Visibility
        InfoItem(
            title = "Visibility",
            value = if(leagueInfo.private) "Private" else "Public",
            icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                {
                    MyIcon(
                        MyIcons.Flip,
                        modifier = Modifier.onTap { viewModel.updateLeagueVisibility(!leagueInfo.private) }
                    )
                }
            } else null
        )

        //League ID
        InfoItem(
            title = "ID",
            value = leagueInfo.id,
            icon = {
                MyIcon(
                    MyIcons.Copy,
                    modifier = Modifier.onTap {
                        clipboardManager.setText(AnnotatedString(leagueInfo.id))
                    }
                )
            }
        )

        // Created At
        InfoItem(
            title = "Time Created",
            value = leagueInfo.createdAt.showDateTime()
        )
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
                    placeholder = { Text(leagueInfo.name) }
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
                    placeholder = { Text(leagueInfo.matchPoints.toString()) }
                )
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
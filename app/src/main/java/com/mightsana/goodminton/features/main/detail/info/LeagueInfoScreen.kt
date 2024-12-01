package com.mightsana.goodminton.features.main.detail.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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

@Composable
@Suppress("unused")
fun LeagueInfoScreen(
    viewModel: DetailViewModel
) {
    val uid = viewModel.user.collectAsState().value.uid
    val participantsUI by viewModel.leagueParticipantsUI.collectAsState()
    val leagueInfo by viewModel.leagueInfo.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val currentUserRole = participantsUI.find { it.user.uid == uid }?.info?.role
    val clipboardManager = LocalClipboardManager.current

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
                        modifier = Modifier.onTap {

                        }
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
        leagueInfo.fixedDouble?.let { fixedDouble ->
            InfoItem(
                title = "Fixed Double",
                value = if(fixedDouble) "Yes" else "No",
                icon = if(currentUserRole == Role.Creator && matches.isEmpty()) {
                    {
                        MyIcon(
                            MyIcons.Flip,
                            modifier = Modifier.onTap { viewModel.updateLeagueFixedDouble(!fixedDouble) }
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
            value = leagueInfo.createdAt.showDateTime(),
            bottomLine = false
        )
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String,
    icon: @Composable (() -> Unit)? = null,
    bottomLine: Boolean = true,
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
    if(bottomLine)
        HorizontalDivider()
}
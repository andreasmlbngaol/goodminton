package com.mightsana.goodminton.features.main.detail.matches

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.ext.secondToTime
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import kotlinx.coroutines.delay

@Composable
fun MatchesScreen(
    viewModel: DetailViewModel,
    onNavigateToParticipant: () -> Unit
) {
    val league by viewModel.leagueJoint.collectAsState()
    val participants by viewModel.leagueParticipantsJoint.collectAsState()
    val user by viewModel.user.collectAsState()
    val matches by viewModel.matches.collectAsState()
    var now by remember { mutableStateOf(Timestamp.now() ) }
    val myRole = participants.find { it.user.uid == user.uid }?.role

    LaunchedEffect(now) {
        delay(1000L)
        now = Timestamp.now()
    }

    if(matches.isNotEmpty())
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Size.padding),
            columns = GridCells.Adaptive(350.dp),
            horizontalArrangement = Arrangement.spacedBy(Size.padding),
            verticalArrangement = Arrangement.spacedBy(Size.padding)
        ) {
            val matchOrdered = matches.sortedBy { it.createdAt }
            items(
                matchOrdered
            ) { match ->
                val order = matchOrdered.map { it.id }.indexOf(match.id)
                val cardContainerColor = MaterialTheme.colorScheme.surfaceContainer
                if(participants.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = cardContainerColor,
                            contentColor = contentColorFor(cardContainerColor)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(bottom = Size.smallPadding),
                            verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header
                            val headerBackgroundColor = when (match.status) {
                                MatchStatus.Scheduled -> MaterialTheme.colorScheme.secondaryContainer
                                MatchStatus.Playing -> MaterialTheme.colorScheme.error
                                MatchStatus.Finished -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(headerBackgroundColor)
                                    .padding(vertical = Size.smallPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Size.extraSmallPadding),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.match_card_label, order + 1),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = contentColorFor(headerBackgroundColor)
                                    )
                                    AnimatedContent(match.status, label = "") { status ->
                                        if (status == MatchStatus.Playing && match.startedAt != null) {
                                            val startedAt = match.startedAt
                                            val timePlaying = now.seconds - startedAt.seconds
                                            Text(
                                                text = stringResource(R.string.match_card_duration_label, timePlaying.secondToTime()),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = contentColorFor(headerBackgroundColor)
                                            )
                                        } else if (status == MatchStatus.Finished && match.finishedAt != null && match.startedAt != null) {
                                            val duration =
                                                match.finishedAt.seconds - match.startedAt.seconds
                                            Text(
                                                text = stringResource(R.string.match_card_duration_label, duration.secondToTime()),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = contentColorFor(headerBackgroundColor)
                                            )
                                        }
                                    }
                                }
                                if(myRole == Role.Creator || myRole == Role.Admin) {
                                    AnimatedContent(
                                        match.status, label = "",
                                        modifier = Modifier
                                            .padding(horizontal = Size.smallPadding)
                                            .align(Alignment.CenterEnd)
                                    ) { status ->
                                        if (status in listOf(
                                                MatchStatus.Scheduled,
                                                MatchStatus.Playing
                                            )
                                        ) {
                                            IconButton(
                                                enabled = !viewModel.isProcessing.collectAsState().value,
                                                onClick = {
                                                    if(status == MatchStatus.Scheduled)
                                                        viewModel.startMatch(match.id)
                                                    else {
                                                        viewModel.validateScore(match) {
                                                            viewModel.finishMatch(match)
                                                        }
                                                    }
                                                },
                                                colors = IconButtonDefaults.iconButtonColors().copy(
                                                    containerColor = contentColorFor(headerBackgroundColor),
                                                    contentColor = headerBackgroundColor
                                                )
                                            ) {
                                                if(status == MatchStatus.Scheduled) MyIcon(MyIcons.Play) else MyIcon(MyIcons.Finished)
                                            }
                                        }
                                    }
                                }
                            }
                            val disabledColor = contentColorFor(cardContainerColor).copy(alpha = 0.3f)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = Size.padding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    match.team1Ids.forEach { id ->
                                        val participant = participants.find { it.id == id }
                                        val textColor = if(participant?.user?.uid == user.uid) MaterialTheme.colorScheme.primaryContainer else contentColorFor(cardContainerColor)
                                        Text(
                                            text = participant?.user?.name.orEmpty(),
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = textColor
                                        )
                                    }
                                }
                                Spacer(Modifier.weight(1f))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AnimatedVisibility(match.status == MatchStatus.Playing && (myRole == Role.Creator || myRole == Role.Admin)) {
                                        IconButton({ viewModel.reduceTeam1Score(match.id) }) {
                                            MyIcon(MyIcons.Minus)
                                        }
                                    }
                                    val scoreColor = if(match.status == MatchStatus.Finished && match.team1Score < match.team2Score)
                                        disabledColor
                                    else contentColorFor(cardContainerColor)

                                    AnimatedContent(
                                        match.team1Score,
                                        transitionSpec = {
                                            slideIntoContainer(
                                                animationSpec = tween(300, easing = EaseIn),
                                                towards = Down
                                            ).togetherWith(
                                                slideOutOfContainer(
                                                    animationSpec = tween(300, easing = EaseOut),
                                                    towards = Up
                                                )
                                            )
                                        },
                                        label = ""
                                    ) { score ->
                                        Text(
                                            score.toString(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = scoreColor
                                        )
                                    }
                                    if(match.status != MatchStatus.Playing || (myRole != Role.Creator && myRole != Role.Admin))
                                        Spacer(Modifier.width(Size.padding))
                                    AnimatedVisibility(match.status == MatchStatus.Playing && (myRole == Role.Creator || myRole == Role.Admin)) {
                                        IconButton({ viewModel.addTeam1Score(match.id) }) {
                                            MyIcon(MyIcons.Plus)
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = Size.padding))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = Size.padding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    match.team2Ids.forEach { id ->
                                        val participant = participants.find { it.id == id }
                                        val textColor = if(participant?.user?.uid == user.uid) MaterialTheme.colorScheme.primaryContainer else contentColorFor(cardContainerColor)
                                        Text(
                                            text = participant?.user?.name ?: "",
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = textColor
                                        )
                                    }
                                }
                                Spacer(Modifier.weight(1f))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AnimatedVisibility(match.status == MatchStatus.Playing && (myRole == Role.Creator || myRole == Role.Admin)) {
                                        IconButton({ viewModel.reduceTeam2Score(match.id) }) {
                                            MyIcon(MyIcons.Minus)
                                        }
                                    }
                                    val scoreColor = if(match.status == MatchStatus.Finished && match.team1Score > match.team2Score)
                                        disabledColor
                                    else contentColorFor(cardContainerColor)

                                    AnimatedContent(
                                        match.team2Score,
                                        transitionSpec = {
                                            slideIntoContainer(
                                                animationSpec = tween(300, easing = EaseIn),
                                                towards = Down
                                            ).togetherWith(
                                                slideOutOfContainer(
                                                    animationSpec = tween(300, easing = EaseOut),
                                                    towards = Up
                                                )
                                            )
                                        },
                                        label = ""
                                    ) { score ->
                                        Text(
                                            score.toString(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = scoreColor
                                        )
                                    }
                                    if(match.status != MatchStatus.Playing || (myRole != Role.Creator && myRole != Role.Admin))
                                        Spacer(Modifier.width(Size.padding))
                                    AnimatedVisibility(match.status == MatchStatus.Playing && (myRole == Role.Creator || myRole == Role.Admin)) {
                                        IconButton({ viewModel.addTeam2Score(match.id) }) {
                                            MyIcon(MyIcons.Plus)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(Size.padding))
                }
            }
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) { Spacer(Modifier.height(150.dp)) }
        }
    else
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val playerPerMatch = if(league.double) 4 else 2
            val participantNotEnough = participants.size < playerPerMatch
            val text = when {
                participantNotEnough -> stringResource(R.string.not_enough_participants)
                else -> stringResource(R.string.no_matches)
            }
            Text(text, style = MaterialTheme.typography.headlineSmall)
            if(participantNotEnough) {
                Spacer(Modifier.height(Size.padding))
                Button(onClick = onNavigateToParticipant) {
                    Text(stringResource(R.string.add_participants_button_label), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
}
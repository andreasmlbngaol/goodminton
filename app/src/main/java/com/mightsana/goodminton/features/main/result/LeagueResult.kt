package com.mightsana.goodminton.features.main.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mightsana.goodminton.features.main.detail.standings.TableData
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import com.mightsana.goodminton.model.ext.showDate
import com.mightsana.goodminton.model.ext.sorted
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.ui.theme.AppTheme
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.Tables
import kotlin.random.Random


@Composable
fun LeagueReport(
    league: LeagueJoint,
    participants: List<LeagueParticipantJoint>,
    participantsStats: List<ParticipantStatsJoint>,
    matches: List<Match>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Size.padding),
        verticalArrangement = Arrangement.spacedBy(Size.padding)
    ) {
        ReportTitle(league.name)
        ReportSubtitle(league, matches, participants.size)
        HorizontalDivider(thickness = 2.dp)
        Champions(participantsStats)
        Spacer(Modifier.height(Size.largePadding))
        StandingsReport(participantsStats)
        Spacer(Modifier.height(Size.largePadding))
        MatchesReport(matches, participants)
    }
}

@Composable
fun RowScope.Podium(
    participant: ParticipantStatsJoint,
    podiumHeight: Dp,
    position: Int,
    superscript: String,
    podiumColor: Color,
    positionBackgroundColor: Color
) {
    val imageSize = 60.dp
    val podiumWidth = 100.dp

    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyImage(
            model = participant.user.profilePhotoUrl,
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
        )
        PrevText(
            text = participant.user.nickname,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .height(podiumHeight)
                .fillMaxWidth()
                .widthIn(min = podiumWidth)
                .background(podiumColor),
            contentAlignment = Alignment.Center
        ) {
            val text = buildAnnotatedString {
                append(position.toString())
                append(
                    AnnotatedString(
                        text = superscript,
                        spanStyle = SpanStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            baselineShift = BaselineShift.Superscript
                        )
                    )
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = contentColorFor(positionBackgroundColor),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(positionBackgroundColor)
                    .padding(Size.smallPadding)
            )
        }
    }
}

@Composable
fun ColumnScope.Champions(participantsStats: List<ParticipantStatsJoint>) {
    val stats = participantsStats.sorted()
    val firstChampion = stats.elementAt(0)
    val secondChampion = stats.elementAt(1)
    val thirdChampion = stats.elementAt(2)

    Text(
        text = "Champions",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Size.padding),
        verticalAlignment = Alignment.Bottom
    ) {
        val firstHeight = 125.dp
        val secondHeight = 100.dp
        val thirdHeight = 80.dp

        Podium(secondChampion, secondHeight, 2, "nd", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary)
        Podium(firstChampion, firstHeight, 1, "st", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
        Podium(thirdChampion, thirdHeight, 3, "rd", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun ColumnScope.ReportTitle(leagueName: String) {
    PrevText(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = leagueName,
        style = MaterialTheme.typography.titleLarge,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun ReportSubtitle(league: LeagueJoint, matches: List<Match>, participantCount: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            PrevText(
                text = "Match Points: ${league.matchPoints}",
                style = MaterialTheme.typography.titleSmall,
            )
            PrevText(
                text = "Deuce: ${if(league.deuceEnabled) "Enabled" else "Disabled"}",
                style = MaterialTheme.typography.titleSmall,
            )
            PrevText(
                text = "Started: ${matches.sortedBy { it.startedAt }.first().startedAt?.showDate()}",
                style = MaterialTheme.typography.titleSmall,
            )
            PrevText(
                text = "Ended: ${matches.sortedBy { it.startedAt }.first().startedAt?.showDate()}",
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Spacer(Modifier.weight(1f))
        Column {
            PrevText(
                text = "Discipline: ${if(league.double) "Double" else "Single"}",
                style = MaterialTheme.typography.titleSmall,
            )
            league.fixedDouble?.let {
                PrevText(
                    text = "Double: ${if(it) "Fixed" else "Random"}",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            PrevText(
                text = "Participants: $participantCount",
                style = MaterialTheme.typography.titleSmall,
            )
            PrevText(
                text = "Created: ${league.createdAt.showDate()}",
                style = MaterialTheme.typography.titleSmall,
            )

        }
    }
}

@Composable
fun ColumnScope.StandingsReport(participantsStats: List<ParticipantStatsJoint>) {
    val tableColumns = listOf("No", "Name", "M", "W", "L", "PS", "PC", "PD")
    val tableData = participantsStats
        .sorted()
        .mapIndexed { index, it ->
            TableData(
                position = index + 1,
                name = it.user.name,
                matches = it.matches,
                wins = it.wins,
                losses = it.losses,
                pointsScored = it.pointsScored,
                pointsConceded = it.pointsConceded,
                pointsDifference = it.pointsScored - it.pointsConceded
            )
        }

    PrevText(
        text = "Standings",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )


    Tables(
        data = tableData,
        enableTableHeaderTitles = true,
        headerTableTitles = tableColumns,
        headerTitlesTextStyle = MaterialTheme.typography.titleMedium,
        headerTitlesBackGroundColor = MaterialTheme.colorScheme.surfaceVariant,
        columnToIndexIncreaseWidth = 1,
        columnToFontWeightModified = mapOf(3 to FontWeight.ExtraBold),
        columnToColorModified = mapOf(
            2 to MaterialTheme.colorScheme.secondary,
            3 to MaterialTheme.colorScheme.primary,
            7 to MaterialTheme.colorScheme.tertiary
        ),
        rowTextStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = Size.smallPadding)
    )
}

@Composable
fun ColumnScope.MatchesReport(matches: List<Match>, participants: List<LeagueParticipantJoint>) {
    PrevText(
        text = "${matches.size} Matches",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    matches.forEach { match ->
        val order = matches.sortedBy { it.createdAt }.map { it.id }.indexOf(match.id)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val headerBackgroundColor = MaterialTheme.colorScheme.primaryContainer
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
                        PrevText(
                            text = "Match ${order + 1}",
                            style = MaterialTheme.typography.titleLarge,
                            color = contentColorFor(headerBackgroundColor)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Size.padding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Size.extraSmallPadding)
                    ) {
                        match.team1Ids.forEach { id ->
                            val participant = participants.find { it.id == id }!!
                            PrevText(
                                text = participant.user.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    PrevText(
                        text = match.team1Score.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = Size.padding))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Size.padding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Size.extraSmallPadding)
                    ) {
                        match.team1Ids.forEach { id ->
                            val participant = participants.find { it.id == id }!!
                            PrevText(
                                text = participant.user.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    PrevText(
                        text = match.team1Score.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(Modifier.height(Size.smallPadding))
            }
        }
    }
}

@PreviewLightDark
@Composable
fun LeagueReportPreview() {
    val creator = MyUser(
        name = "Creator User",
        nickname = "Creator",
        uid = "creatorId",
        username = "creator.username",
        profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJ4pKbU0GcxcGswmbvvxL6gNzr1vL3Jubpl9DSiCcQ9f-Scjg=s96-c"
    )
    val league = LeagueJoint(
        id = "leagueId",
        name = "Dummy League",
        matchPoints = 21,
        private = true,
        deuceEnabled = true,
        double = true,
        fixedDouble = true,
        status = LeagueStatus.Finished,
        createdBy = creator
    )
    val users = mutableListOf<MyUser>()
    users.add(creator)
    for(i in 1..5) {
        users.add(
            MyUser(
                name = "Participant $i",
                nickname = "Participant",
                uid = "participantId$i",
                username = "username_$i",
                profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJV_56zsypTdm1mqaa7V0uRhutaOLXqa7_K8-xtpPbPZkuNtA=s96-c"
            )
        )
    }

    // Participants Size 6
    val participants = mutableListOf<LeagueParticipantJoint>()
    val participantsStats = mutableListOf<ParticipantStatsJoint>()
    val matchesCount = Random.nextInt(1, 15)
    users.forEachIndexed { index, user ->
        participants.add(
            LeagueParticipantJoint(
                id = "participantId${index + 1}",
                league = league,
                user = user
            )
        )
        val winsCount = Random.nextInt(0,matchesCount)
        val lossesCount = matchesCount - winsCount
        val pointsScored = matchesCount * (Random.nextInt(0, league.matchPoints))
        val pointsConceded = matchesCount * (Random.nextInt(0, league.matchPoints))
        participantsStats.add(
            ParticipantStatsJoint(
                id = "statsId${index + 1}",
                user = user,
                league = league,
                wins = winsCount,
                losses = lossesCount,
                pointsScored = pointsScored,
                pointsConceded = pointsConceded,
                matches = matchesCount
            )
        )
    }
    val matches = mutableListOf<Match>()
    repeat(matchesCount) { count ->
        val team1Ids = listOf(
            participants.random().id,
            participants.random().id
        )
        val team2Ids = listOf(
            participants.random().id,
            participants.random().id
        )
        matches.add(
            Match(
                id = "matchId$count",
                leagueId = league.id,
                team1Ids = team1Ids,
                team2Ids = team2Ids,
                team1Score = Random.nextInt(0, league.matchPoints),
                team2Score = Random.nextInt(0, league.matchPoints),
                startedAt = Timestamp.now(),
                finishedAt = Timestamp.now(),
                status = MatchStatus.Finished
            )
        )
    }

    AppTheme {
        LeagueReport(
            league = league,
            participants = participants,
            participantsStats = participantsStats,
            matches = matches
        )
    }
}

@Composable
fun PrevText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text,
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        style
    )
}

package com.mightsana.goodminton.features.main.detail.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import com.mightsana.goodminton.features.main.result.Champions
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.Tables

@Suppress("unused")
@Composable
fun StandingsScreen(
    viewModel: DetailViewModel
) {
    val participantsStats by viewModel.participantsStats.collectAsState()
    val league by viewModel.leagueJoint.collectAsState()
    val tableData = participantsStats
        .sortedWith(
            compareByDescending<ParticipantStatsJoint> { it.wins }
                .thenBy { it.matches }
                .thenBy { it.losses }
                .thenByDescending { (it.pointsScored - it.pointsConceded) }
                .thenByDescending { it.pointsScored }
                .thenBy { it.pointsConceded }
                .thenBy { it.user.name }
        )
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
    val tableColumns = listOf(
        stringResource(R.string.standings_number),
        stringResource(R.string.standings_name),
        stringResource(R.string.standings_matches),
        stringResource(R.string.standings_wins),
        stringResource(R.string.standings_losses),
        stringResource(R.string.standings_points_scored),
        stringResource(R.string.standings_points_conceded),
        stringResource(R.string.standings_points_difference)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(Size.smallPadding),
        verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(participantsStats.isNotEmpty() && league.status == LeagueStatus.Finished) {
            Champions(participantsStats)
            Spacer(Modifier.height(Size.padding))
        }
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
                3 to MaterialTheme.colorScheme.primaryContainer,
                7 to MaterialTheme.colorScheme.tertiary
            ),
            rowTextStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = Size.smallPadding)
        )
    }

}

data class TableData(
    val position: Int,
    val name: String,
    val matches: Int,
    val wins: Int,
    val losses: Int,
    val pointsScored: Int,
    val pointsConceded: Int,
    val pointsDifference: Int
)
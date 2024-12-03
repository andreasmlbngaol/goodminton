package com.mightsana.goodminton.features.main.detail.matches

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mightsana.goodminton.features.main.detail.DetailViewModel

@Composable
fun MatchesScreen(viewModel: DetailViewModel) {
    val participantsJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val matchesJoint by viewModel.matchesJoint.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(matchesJoint) { match ->
            match.team1.forEach {
                Text(text = it.nickname)
            }
            match.team2.forEach {
                Text(text = it.nickname)
            }
        }
    }
}
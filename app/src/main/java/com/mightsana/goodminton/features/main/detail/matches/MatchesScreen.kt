package com.mightsana.goodminton.features.main.detail.matches

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.features.main.detail.DetailViewModel

@Composable
fun MatchesScreen(viewModel: DetailViewModel) {
//    val participantsJoint by viewModel.leagueParticipantsJoint.collectAsState()
    val matchesJoint by viewModel.matchesJoint.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(matchesJoint.sortedBy { it.createdAt }) { match ->
            match.team1.forEach {
                Text(text = "${it.name} -> ${if(it.openToAdd) "Open" else "Closed"}")
            }
            match.team2.forEach {
                Text(text = "${it.name} -> ${if(it.openToAdd) "Open" else "Closed"}")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
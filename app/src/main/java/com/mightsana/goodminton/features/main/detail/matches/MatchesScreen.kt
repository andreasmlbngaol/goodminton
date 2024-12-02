package com.mightsana.goodminton.features.main.detail.matches

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mightsana.goodminton.features.main.detail.DetailViewModel

@Composable
fun MatchesScreen(viewModel: DetailViewModel) {
    val participantsJoint by viewModel.leagueParticipantsJoint.collectAsState()

    LazyColumn {
        items(participantsJoint) { item ->
            Log.d("MatchesScreen", item.toString())
            Text(text = item.user.uid)
        }
    }
}
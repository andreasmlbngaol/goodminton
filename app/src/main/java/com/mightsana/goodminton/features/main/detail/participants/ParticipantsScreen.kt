package com.mightsana.goodminton.features.main.detail.participants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.LeagueParticipants
import com.mightsana.goodminton.model.repository.users.MyUser

@Composable
@Suppress("unused")
fun ParticipantsScreen(
    participantUsers: List<MyUser>,
    participantInfos: List<LeagueParticipants>,
    viewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        participantUsers.forEachIndexed { index, participant ->
            Text("${index + 1}. ${participant.name}")
            Text(participant.nickname)
            Text(participant.username)
            Text(participant.email)
            Text(participantInfos.find { it.userId == participant.uid }!!.role.name)
        }
    }
}
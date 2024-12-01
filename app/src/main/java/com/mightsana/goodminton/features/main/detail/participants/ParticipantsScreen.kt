package com.mightsana.goodminton.features.main.detail.participants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mightsana.goodminton.features.main.detail.DetailViewModel
import com.mightsana.goodminton.features.main.model.LeagueParticipantsUI

@Composable
@Suppress("unused")
fun ParticipantsScreen(
    participantsUI: List<LeagueParticipantsUI>,
    viewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        participantsUI.forEach { participant ->
            Text("Uid: ${participant.user.uid}", maxLines = 1)
            Text("Name: ${participant.user.name}", maxLines = 1)
            Text("Nickname: ${participant.user.nickname}", maxLines = 1)
            Text("Username: ${participant.user.username}", maxLines = 1)
            Text("Email: ${participant.user.email}", maxLines = 1)
            Text("Photo URL: ${participant.user.profilePhotoUrl}", maxLines = 1)
            Text("Phone Number: ${participant.user.phoneNumber}", maxLines = 1)
            Text("Bio: ${participant.user.bio}", maxLines = 1)
            Text("Birthday: ${participant.user.birthDate.toString()}", maxLines = 1)
            Text("Gender: ${participant.user.gender}", maxLines = 1)
            Text("Address: ${participant.user.address}", maxLines = 1)
            Text("Created At: ${participant.user.createdAt}", maxLines = 1)
            Text("Is Verified: ${participant.user.verified}", maxLines = 1)
            Text("\n")
        }
    }
}
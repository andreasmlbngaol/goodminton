package com.mightsana.goodminton.features.main.detail.info

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
fun LeagueInfoScreen(
    participantsUI: List<LeagueParticipantsUI>,
    viewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        participantsUI.forEach { participantUI ->
            Text("Uid: ${participantUI.user.uid}", maxLines = 1)
            Text("Name: ${participantUI.user.name}", maxLines = 1)
            Text("Nickname: ${participantUI.user.nickname}", maxLines = 1)
            Text("Username: ${participantUI.user.username}", maxLines = 1)
            Text("Email: ${participantUI.user.email}", maxLines = 1)
            Text("Photo URL: ${participantUI.user.profilePhotoUrl}", maxLines = 1)
            Text("Phone Number: ${participantUI.user.phoneNumber}", maxLines = 1)
            Text("Bio: ${participantUI.user.bio}", maxLines = 1)
            Text("Birthday: ${participantUI.user.birthDate.toString()}", maxLines = 1)
            Text("Gender: ${participantUI.user.gender}", maxLines = 1)
            Text("Address: ${participantUI.user.address}", maxLines = 1)
            Text("Created At: ${participantUI.user.createdAt}", maxLines = 1)
            Text("Is Verified: ${participantUI.user.verified}", maxLines = 1)
            Text("\n")
        }
    }
}
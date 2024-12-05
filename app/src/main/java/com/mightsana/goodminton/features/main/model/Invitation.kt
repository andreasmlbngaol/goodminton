package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class Invitation(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val leagueId: String = "",
    val invitedAt: Timestamp = Timestamp.now()
)

data class InvitationJoint(
    val id: String = "",
    val sender: MyUser = MyUser(),
    val receiver: MyUser = MyUser(),
    val league: LeagueJoint = LeagueJoint(),
    val invitedAt: Timestamp = Timestamp.now()
)
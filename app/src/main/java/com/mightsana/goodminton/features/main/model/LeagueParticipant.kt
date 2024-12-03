package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class LeagueParticipant(
    val id: String = "",
    val leagueId: String = "",
    val userId: String = "",
    val role: Role = Role.Player,
    val status: Status = Status.Active,
    val participateAt: Timestamp = Timestamp.now()
)

data class LeagueParticipantJoint(
    val id: String = "",
    val league: LeagueJoint = LeagueJoint(),
    val user: MyUser = MyUser(),
    val role: Role = Role.Player,
    val status: Status = Status.Active,
    val participateAt: Timestamp = Timestamp.now(),
)

data class LeagueParticipantUI(
    val info: LeagueParticipant = LeagueParticipant(),
    val user: MyUser = MyUser(),
    val stats: ParticipantStats = ParticipantStats()
)

enum class Role {
    Creator, Admin, Player, Spectator
}

enum class Status {
    Pending, Active, Invited
}
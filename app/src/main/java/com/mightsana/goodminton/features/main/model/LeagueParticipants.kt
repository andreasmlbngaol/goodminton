package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class LeagueParticipants(
    val id: String = "",
    val leagueId: String = "",
    val userId: String = "",
    val role: Role = Role.Player,
    val status: Status = Status.Active,
    val participateAt: Timestamp = Timestamp.now()
)

@Suppress("unused")
data class LeagueParticipantsUI(
    val info: LeagueParticipants,
    val user: MyUser
)
@Suppress("unused")
enum class Role {
    Creator, Admin, Player, Spectator
}

@Suppress("unused")
enum class Status {
    Pending, Active
}
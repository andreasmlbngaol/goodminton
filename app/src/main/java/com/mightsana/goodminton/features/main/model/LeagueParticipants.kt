package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friends.Friend
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
    val info: LeagueParticipants = LeagueParticipants(),
    val user: MyUser = MyUser(),
    val stats: ParticipantStats = ParticipantStats()
)
@Suppress("unused")
enum class Role {
    Creator, Admin, Player, Spectator
}

@Suppress("unused")
enum class Status {
    Pending, Active
}
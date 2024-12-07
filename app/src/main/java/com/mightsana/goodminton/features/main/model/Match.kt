package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class Match(
    val id: String = "",
    val leagueId: String = "",
    val team1Ids: List<String> = emptyList(),
    val team2Ids: List<String> = emptyList(),
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val startedAt: Timestamp? = null,
    val finishedAt: Timestamp? = null,
    val status: MatchStatus = MatchStatus.Scheduled
)

data class MatchJoint(
    val id: String = "",
    val league: LeagueJoint = LeagueJoint(),
    val team1: List<MyUser> = emptyList(),
    val team2: List<MyUser> = emptyList(),
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val startedAt: Timestamp? = null,
    val finishedAt: Timestamp? = null,
    val duration: Timestamp? = null,
    val status: MatchStatus = MatchStatus.Scheduled
)

@Suppress("unused")
enum class MatchStatus {
    Scheduled, Playing, Finished
//    , Cancelled
}
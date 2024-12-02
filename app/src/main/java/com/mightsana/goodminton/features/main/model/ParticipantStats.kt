package com.mightsana.goodminton.features.main.model

import com.mightsana.goodminton.model.repository.users.MyUser

data class ParticipantStats(
    val id: String = "",
    val userId: String = "",
    val leagueId: String = "",
    val wins: Int = 0,
    val losses: Int = 0,
    val pointsScored: Int = 0,
    val pointsConceded: Int = 0,
    val matches: Int = 0
)

data class ParticipantStatsJoint(
    val id: String = "",
    val user: MyUser = MyUser(),
    val league: League = League(),
    val wins: Int = 0,
    val losses: Int = 0,
    val pointsScored: Int = 0,
    val pointsConceded: Int = 0,
    val matches: Int = 0
)
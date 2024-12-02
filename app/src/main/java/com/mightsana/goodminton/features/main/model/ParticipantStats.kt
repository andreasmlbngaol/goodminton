package com.mightsana.goodminton.features.main.model

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
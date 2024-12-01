package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp

data class Match(
    val id: String = "",
    val team1: List<String> = emptyList(),
    val team2: List<String> = emptyList(),
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val startedAt: Timestamp? = null,
    val finishedAt: Timestamp? = null,
    val duration: Timestamp? = null,
    val status: MatchStatus = MatchStatus.Scheduled
)

@Suppress("unused")
enum class MatchStatus {
    Scheduled, Playing, Finished, Cancelled
}
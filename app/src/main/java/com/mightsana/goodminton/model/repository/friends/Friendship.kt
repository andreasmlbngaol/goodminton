package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp

data class Friendship(
    val users: List<String> = emptyList(),
    val startedAt: Timestamp = Timestamp.now()
)

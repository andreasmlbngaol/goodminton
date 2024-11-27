package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp

data class Friend(
    val friendId: String = "",
    val startedAt: Timestamp = Timestamp.now()
)

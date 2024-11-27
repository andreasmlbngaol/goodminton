package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.OneUser

@Suppress("unused")
data class FriendStatus(
    val friend: OneUser = OneUser(),
    val startedAt: Timestamp = Timestamp.now()
)
package com.mightsana.goodminton.model.repository.friend_requests

import com.google.firebase.Timestamp

data class FriendRequest(
    val senderId: String = "",
    val receiverId: String = "",
    val requestedAt: Timestamp = Timestamp.now()
)
package com.mightsana.goodminton.model.repository.friend_requests

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class FriendRequest(
    val senderId: String = "",
    val receiverId: String = "",
    val requestedAt: Timestamp = Timestamp.now()
)

data class FriendRequestJoint(
    val sender: MyUser = MyUser(),
    val receiver: MyUser = MyUser(),
    val request: FriendRequest = FriendRequest()
)
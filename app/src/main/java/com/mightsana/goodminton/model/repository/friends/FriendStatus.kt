package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

@Suppress("unused")
data class FriendStatus(
    val friend: MyUser = MyUser(),
    val startedAt: Timestamp = Timestamp.now()
)
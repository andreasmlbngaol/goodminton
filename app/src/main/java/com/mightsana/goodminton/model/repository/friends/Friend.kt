package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class Friend(
    val ids: List<String> = emptyList(),
    val startedAt: Timestamp = Timestamp.now()
)

data class FriendJoint(
    val users: List<MyUser> = emptyList(),
    val startedAt: Timestamp = Timestamp.now()
)

data class FriendUI(
    val info: MyUser = MyUser(),
    val data: Friend = Friend()
)
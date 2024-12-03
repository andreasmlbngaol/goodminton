package com.mightsana.goodminton.model.repository.friends

import com.google.firebase.Timestamp
import com.mightsana.goodminton.model.repository.users.MyUser

data class Friend(
    val id: String = "",
    val usersIds: List<String> = emptyList(),
    val startedAt: Timestamp = Timestamp.now()
)

data class FriendJoint(
    val id: String = "",
    val user: MyUser = MyUser(),
    val startedAt: Timestamp = Timestamp.now()
)

data class FriendUI(
    val info: MyUser = MyUser(),
    val data: Friend = Friend()
)
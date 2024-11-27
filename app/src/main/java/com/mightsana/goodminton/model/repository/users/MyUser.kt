package com.mightsana.goodminton.model.repository.users

data class MyUser(
    val uid: String = "",
    val name: String = "",
    val nickname: String = "",
    val username: String = "",
    val email: String = "",
    val profilePhotoUrl: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val birthDate: Long? = null,
    val gender: String? = null,
    val address: String? = null,
    val createdAt: Long = 0L,
    val verified: Boolean = false
)

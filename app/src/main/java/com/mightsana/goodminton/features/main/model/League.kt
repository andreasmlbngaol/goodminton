package com.mightsana.goodminton.features.main.model

import com.google.firebase.Timestamp

data class League(
    val id: String = "",
    val name: String = "",
    val matchPoints: Int = 0,
    val deuceEnabled: Boolean = true,
    val isDouble: Boolean = false,
    val isFixedDouble: Boolean? = null,
    val createdById: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
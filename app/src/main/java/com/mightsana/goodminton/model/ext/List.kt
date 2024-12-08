package com.mightsana.goodminton.model.ext

import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint

@Suppress("unused")
fun <T> Int.isLastIndexOf(list: List<T>): Boolean = this == list.lastIndex

fun List<ParticipantStatsJoint>.sorted(): List<ParticipantStatsJoint> =
    this.sortedWith(
        compareByDescending<ParticipantStatsJoint> { it.wins }
            .thenBy { it.matches }
            .thenBy { it.losses }
            .thenByDescending { (it.pointsScored - it.pointsConceded) }
            .thenByDescending { it.pointsScored }
            .thenBy { it.pointsConceded }
            .thenBy { it.user.name }
    )
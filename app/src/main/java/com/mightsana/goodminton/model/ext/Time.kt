package com.mightsana.goodminton.model.ext

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Timestamp.showDate(): String {
    val instant = Instant.ofEpochSecond(this.seconds)
//    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}
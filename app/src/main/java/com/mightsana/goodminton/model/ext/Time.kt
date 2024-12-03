package com.mightsana.goodminton.model.ext

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Timestamp.showDate(): String {
    val instant = Instant.ofEpochSecond(this.seconds)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun Timestamp.showDateTime(): String {
    val instant = Instant.ofEpochSecond(this.seconds)
    val formatter = DateTimeFormatter.ofPattern("EEEE, HH:mm:ss\nd MMMM yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

@Suppress("unused")
fun Long.showDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd MMM yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

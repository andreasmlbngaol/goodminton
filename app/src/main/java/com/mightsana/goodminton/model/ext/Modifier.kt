package com.mightsana.goodminton.model.ext

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onTap(onTap: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onTap = {
            onTap()
        }
    )
}

@Suppress("unused")
fun Modifier.onLongPress(onLongPress: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onLongPress = {
            onLongPress()
        }
    )
}

@Suppress("unused")
fun Modifier.onDoubleTap(onDoubleTap: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onDoubleTap = {
            onDoubleTap()
        }
    )
}

fun Modifier.onGesture(
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null
): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onTap = onTap?.let { { it() } },
        onLongPress = onLongPress?.let { { it() } },
        onDoubleTap = onDoubleTap?.let { { it() } }
    )
}
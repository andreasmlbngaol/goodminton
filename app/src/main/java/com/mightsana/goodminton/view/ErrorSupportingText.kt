package com.mightsana.goodminton.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorSupportingText(
    visible: Boolean,
    message: String,
) {
    AnimatedVisibility(visible) {
        Text(message)
    }
}
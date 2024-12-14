package com.mightsana.goodminton.model.ext

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExitWithDoublePress() {
    val context = LocalContext.current
    val lastBackPressTime = remember { mutableLongStateOf(0L) }
    val delay = 2000L

    BackHandler {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastBackPressTime.longValue < delay) {
            (context as? Activity)?.finish()
        } else {
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            lastBackPressTime.longValue = currentTime
        }
    }
}
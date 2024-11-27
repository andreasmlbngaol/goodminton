package com.mightsana.goodminton.model.ext

import android.app.Application
import android.widget.Toast
import androidx.annotation.StringRes

fun Application.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Application.toast(@StringRes messageStringRes: Int) {
    Toast.makeText(this, this.getString(messageStringRes), Toast.LENGTH_SHORT).show()
}

package com.mightsana.goodminton.model.ext

import kotlinx.coroutines.flow.MutableStateFlow

fun MutableStateFlow<String>.clip(): String {
    return this.value.trim()
}

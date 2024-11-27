package com.mightsana.goodminton.model.ext

fun String.noSpace(): String {
    return this.replace(" ", "")
}
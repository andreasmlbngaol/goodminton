package com.mightsana.goodminton.model.ext

fun String.noSpace(): String {
    return this.replace(" ", "")
}

fun String.censoredEmail(): String {
    val index = this.indexOf("@")
    val name = this.substring(0, index)
    val domain = this.substring(index)
    return name.replaceRange(2, name.length - 1, "****") + domain
}

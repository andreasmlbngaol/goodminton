package com.mightsana.goodminton.model.ext

@Suppress("unused")
fun <T> Int.isLastIndexOf(list: List<T>): Boolean = this == list.lastIndex
package com.codeleg.dailyscope.utils

data class FilterState(
    val date: Long? = null,
    val category: String? = null,
    val sentimentMin: Float = -1f,
    val sentimentMax: Float = 1f
)
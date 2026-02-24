package com.codeleg.dailyscope.database.model

data class NewsResponse(
    val message: String,
    val news: List<News>,
    val success: Boolean
)
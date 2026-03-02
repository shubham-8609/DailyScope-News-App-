package com.codeleg.dailyscope.database.model

data class TopNewsResponse(
    val country: String,
    val language: String,
    val top_news: List<TopNews>
)
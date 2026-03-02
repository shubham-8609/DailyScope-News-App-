package com.codeleg.dailyscope.database.model

data class SearchNewsResponse(
    val offset: Int,
    val number: Int,
    val available: Int,
    val news: List<Article>
)
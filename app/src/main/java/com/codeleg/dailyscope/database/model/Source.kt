package com.codeleg.dailyscope.database.model

data class Source(
    val hostname: String,
    val id: String,
    val name: String?,
    val website: String?
)
package com.codeleg.dailyscope.database.model

data class News(
    val _id: String,
    val advertisementLabel: String?,
    val categories: List<String>,
    val createdAt: String,
    val description: String,
    val isAdvertisement: Boolean,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val updatedAt: String,
    val url: String,
    val urlToImage: String?
)
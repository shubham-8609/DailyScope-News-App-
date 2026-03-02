package com.codeleg.dailyscope.database.model

import com.google.gson.annotations.SerializedName

data class Article(
    val id: Long,
    val title: String,
    val text: String,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    @SerializedName("publish_date")
    val publishDate: String,
    val authors: List<String>?,
    val category: String?,
    val language: String?,
    @SerializedName("source_country")
    val sourceCountry: String?,
    val sentiment: Double?
)

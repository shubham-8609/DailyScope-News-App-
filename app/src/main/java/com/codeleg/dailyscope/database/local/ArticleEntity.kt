package com.codeleg.dailyscope.database.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "articles",
    indices = [Index(value = ["category"]), Index(value = ["publishDate"])]
)
data class ArticleEntity(
    val id: Long,
    val title: String,
    val text: String,
    val summary: String?,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val isBookmarked: Boolean = false,
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
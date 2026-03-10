package com.codeleg.dailyscope.database.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Long,
    val title: String,
    val text: String,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    var isBookmarked: Boolean = false,
    @SerializedName("publish_date")
    val publishDate: String,
    val authors: List<String>?,
    val category: String?,
    val language: String?,
    @SerializedName("source_country")
    val sourceCountry: String?,
    val sentiment: Double?
) : Parcelable
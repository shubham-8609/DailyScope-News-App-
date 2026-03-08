package com.codeleg.dailyscope.database.network

import com.codeleg.dailyscope.database.model.RetrieveNewsResponse
import com.codeleg.dailyscope.database.model.TopNewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-news")
    suspend fun getLatestNews(
        @Query("source-country") country: String,
        @Query("language")language: String?,
        @Query("headlines-only")headlinesOnly: Boolean = false
    ): TopNewsResponse

        @GET("retrieve-news")
        suspend fun retrieveNews(@Query("ids") id: Long): RetrieveNewsResponse



}
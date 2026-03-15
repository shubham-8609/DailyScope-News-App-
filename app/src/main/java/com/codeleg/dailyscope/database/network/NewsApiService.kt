package com.codeleg.dailyscope.database.network

import com.codeleg.dailyscope.database.model.RetrieveNewsResponse
import com.codeleg.dailyscope.database.model.SearchNewsResponse
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

        @GET("search-news")
        suspend fun searchNews(
            @Query("text") query: String,
            @Query("offset") offset: Int,
            @Query("number") pageSize: Int,
            @Query("language") language: String? = null
        ): SearchNewsResponse



}
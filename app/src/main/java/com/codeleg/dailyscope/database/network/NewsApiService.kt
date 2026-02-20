package com.codeleg.dailyscope.database.network

import com.codeleg.dailyscope.database.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("latest")
    suspend fun getLatestNews(): Response<NewsResponse>

    @GET("local")
    suspend fun getLocalNews(): Response<NewsResponse>

     @GET("technologies")
    suspend fun getTechnologyNews(): Response<NewsResponse>

     @GET("sports")
    suspend fun getSportsNews(): Response<NewsResponse>

    @GET("news")
    suspend fun getNews(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
    ): Response<NewsResponse>

    @GET("business")
    suspend fun getBusinessNews(): Response<NewsResponse>

    @GET("agriculture")
    suspend fun getAgricultureNews(): Response<NewsResponse>



}
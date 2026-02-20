package com.codeleg.dailyscope.database.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://news.knowivate.com/api/"
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val newApi: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

}
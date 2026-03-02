package com.codeleg.dailyscope.database.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.worldnewsapi.com/"
    private const val API_KEY = "fcdb8a062e524f8ba1d8d8889bd06e31"


    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter("api-key", API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

        val newsApi: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

}
package com.codeleg.dailyscope.database.repository

import com.codeleg.dailyscope.database.model.NewsResponse
import com.codeleg.dailyscope.database.network.NewsApiService
import com.codeleg.dailyscope.utils.Resource

class NewsRepository(
    private val newsApi: NewsApiService
) {
    suspend fun getLatestNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getLatestNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }




}
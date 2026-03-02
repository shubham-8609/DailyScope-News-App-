package com.codeleg.dailyscope.database.repository

import com.codeleg.dailyscope.database.model.TopNewsResponse
import com.codeleg.dailyscope.database.network.NewsApiService
import com.codeleg.dailyscope.utils.Resource

class NewsRepository(
    private val newsApi: NewsApiService
) {
    suspend fun getTopNews(
        country: String = "in",
        language: String = "en",
        headlinesOnly: Boolean
    ): Resource<TopNewsResponse> {
        return try {
            val response = newsApi.getLatestNews(
                country = country,
                language = language,
                headlinesOnly = headlinesOnly
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error--Found at repo layer")
        }
    }

    /*suspend fun getLocalNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getLocalNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong. Response isn't successful or body is null.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error")
        }
    }

    suspend fun getTechnologyNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getTechnologyNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong. Response isn't successful or body is null.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error")
        }
    }

    suspend fun getSportsNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getSportsNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong. Response isn't successful or body is null.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error")
        }
    }

    suspend fun getBusinessNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getBusinessNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong. Response isn't successful or body is null.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error")
        }
    }

    suspend fun getAgricultureNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getAgricultureNews()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Something went wrong. Response isn't successful or body is null.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get error")
        }
    }*/

}
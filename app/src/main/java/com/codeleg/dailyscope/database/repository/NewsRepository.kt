package com.codeleg.dailyscope.database.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.codeleg.dailyscope.database.local.NewsDao
import com.codeleg.dailyscope.database.local.toArticle
import com.codeleg.dailyscope.database.local.toEntity
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.network.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class NewsRepository(
    private val newsApi: NewsApiService,
    private val newsDao: NewsDao
) {


    fun getNewsFromDb(): Flow<List<Article>>  {
        return newsDao.getAllArticles().map { entityList ->
                entityList.map { it.toArticle() }
        }
    }

    fun getPagedNewsFromDb(): Flow<PagingData<Article>>{

        return Pager(
        config = PagingConfig(pageSize = 15 , enablePlaceholders = false),
        pagingSourceFactory = { newsDao.getPagedArticles() }
        ).flow.map { pagingData ->
            pagingData.map { it.toArticle() }
        }

    }

    suspend fun refreshNews(
        country: String = "in",
        language: String = "en",
        headlinesOnly: Boolean
    ): Boolean  {
        return try {
            Log.d("codeleg", "Refreshing news from API...")
            val response = newsApi.getLatestNews(
                country = country,
                language = language,
                headlinesOnly = headlinesOnly
            )

            val articles = response.top_news.flatMap { it.news }
            Log.d("codeleg", "Fetched ${articles.size} articles from API --newsRepo")

            if (articles.isEmpty()) Log.d("codeleg", "Api returned empty list   --newsRepo")


            // Map DTO -> Entity
            newsDao.insertArticles(
                articles.map { it.toEntity() }
            )
            Log.d("codeleg", "Inserted ${articles.size} articles into DB")
            true
        } catch (e: Exception) {
            Log.e("codeleg", "Error refreshing news: ${e.localizedMessage}", e)
            false
        }
    }

    suspend fun clearDB()  = newsDao.deleteAllArticles()


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
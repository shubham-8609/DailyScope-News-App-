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
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class NewsRepository(
    private val newsApi: NewsApiService,
    private val newsDao: NewsDao
) {




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

    suspend fun getCategoriesFromDb(): List<String?> = newsDao.getCategories()

    fun getFilteredNews(filter: FilterState): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = {
                newsDao.getFilteredNews(
                    category = filter.category,
                    date = filter.date,
                    sentimentMin = filter.sentimentMin,
                    sentimentMax = filter.sentimentMax
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toArticle() }
        }
    }

    suspend fun clearDB()  = newsDao.deleteAllArticles()



}
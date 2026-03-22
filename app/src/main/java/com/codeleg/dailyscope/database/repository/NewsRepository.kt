package com.codeleg.dailyscope.database.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.codeleg.dailyscope.database.local.ArticleEntity
import com.codeleg.dailyscope.database.local.NewsDao
import com.codeleg.dailyscope.database.local.toArticle
import com.codeleg.dailyscope.database.local.toEntity
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.database.network.NewsApiService
import com.codeleg.dailyscope.database.paging.SearchNewsPagingSource
import com.codeleg.dailyscope.utils.FilterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NewsRepository(
    private val newsApi: NewsApiService,
    private val newsDao: NewsDao,
    private val settingsRepo: SettingsRepository
) {




    fun getPagedNewsFromDb(): Flow<PagingData<Article>>{

        return Pager(
        config = PagingConfig(pageSize = 15 , enablePlaceholders = false),
        pagingSourceFactory = { newsDao.getPagedArticles() }
        ).flow.map { pagingData ->
            pagingData.map { it.toArticle() }
        }

    }

    fun getBookmarkedArticles(): Flow<List<Article>> =
        newsDao.getBookmarkedArticles().map { entities -> entities.map { it.toArticle() } }
    suspend fun refreshNews(
        country: String = "in",
        language: String = "en",
        headlinesOnly: Boolean
    ): Int  {
         try {
            Log.d("codeleg", "Refreshing news from API...")
            val response = newsApi.getLatestNews(
                country = country ,
                language = language,
                headlinesOnly = headlinesOnly
            )

            val articles = response.top_news.flatMap { it.news }
            Log.d("codeleg", "Fetched ${articles.size} articles from API --newsRepo")

            if (articles.isEmpty()) Log.d("codeleg", "Api returned empty list   --newsRepo")

            val bookmarkedUrls = newsDao.getBookmarkedUrls().toSet()
            newsDao.insertArticles(
                articles.map { article ->
                    val isBookmarked = bookmarkedUrls.contains(article.url)
                    article.copy(isBookmarked = isBookmarked).toEntity()
                }
            )
            Log.d("codeleg", "Inserted ${articles.size} articles into DB")
            return articles.size
        } catch (e: Exception) {
            Log.e("codeleg", "Error refreshing news: ${e.localizedMessage}", e)
             return -1
        }
    }


    suspend fun getCategoriesFromDb(): List<String?> = newsDao.getCategories()

    fun searchNews(
        query: String,
        language: String? = "en"
    ): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchNewsPagingSource(
                    api = newsApi,
                    newsDao = newsDao,
                    query = query,
                    language = language
                )
            }
        ).flow
    }

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

    suspend fun clearBookmarks() = newsDao.clearBookmarks()

    suspend fun getTotalNewsCount() = newsDao.getTotalNewsCount()

    suspend fun getGoodNews() = newsDao.getGoodNews().map { it.toArticle() }

    suspend fun getBadNews() = newsDao.getBadNews().map { it.toArticle() }

    suspend fun  getNotifiedArticles() = newsDao.getNotifiedArticles()

    suspend fun updateArticles(article: ArticleEntity) = newsDao.updateArticles(article)

    suspend fun setBookmarkState(article: Article) = withContext(Dispatchers.IO) {
        newsDao.insertArticles(listOf(article.toEntity()))
    }
}
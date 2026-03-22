package com.codeleg.dailyscope.database.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao

interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("UPDATE articles SET isBookmarked = :state WHERE url = :url")
    suspend fun updateBookmark(url: String, state: Boolean)

    @Query("SELECT * FROM articles ORDER BY publishDate DESC")
    fun getPagedArticles(): PagingSource<Int , ArticleEntity>

    @Query("SELECT DISTINCT category FROM articles")
    suspend fun getCategories(): List<String?>

    // Getting total number of news
    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getTotalNewsCount(): Int

    @Query("""
SELECT * FROM articles
WHERE (:category IS NULL OR category = :category)
AND (:date IS NULL OR publishDate >= :date)
AND sentiment BETWEEN :sentimentMin AND :sentimentMax
ORDER BY publishDate DESC
""")
    fun getFilteredNews(
        category: String?,
        date: Long?,
        sentimentMin: Float,
        sentimentMax: Float
    ): PagingSource <Int, ArticleEntity>

    @Query("""
SELECT * FROM articles
WHERE isBookmarked = 1
ORDER BY publishDate DESC
""")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT url FROM articles WHERE isBookmarked = 1")
    suspend fun getBookmarkedUrls(): List<String>


    @Query("UPDATE articles SET isBookmarked = 0 WHERE isBookmarked = 1")
    suspend fun clearBookmarks()

    @Query("SELECT * FROM articles  WHERE sentiment >= 0.8 ORDER BY publishDate DESC")
    suspend fun  getGoodNews(): List<ArticleEntity>

    @Query("SELECT * FROM articles  WHERE sentiment <= -0.7 ORDER BY publishDate DESC")
    suspend fun getBadNews(): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE isNotified = 1 ORDER BY publishDate DESC")
    suspend fun getNotifiedArticles(): List<ArticleEntity>

    @Update
    suspend fun updateArticles(article : ArticleEntity)

}
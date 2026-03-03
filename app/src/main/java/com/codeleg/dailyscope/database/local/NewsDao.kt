package com.codeleg.dailyscope.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao

interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM articles ORDER BY publishDate DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("UPDATE articles SET isBookmarked = :state WHERE url = :url")
    suspend fun updateBookmark(url: String, state: Boolean)

    @Query("SELECT * FROM articles WHERE isBookmarked = 1")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>


}
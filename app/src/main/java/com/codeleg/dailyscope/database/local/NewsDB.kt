package com.codeleg.dailyscope.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codeleg.dailyscope.database.model.Article

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
@TypeConverters(Convertors::class)
abstract class NewsDB : RoomDatabase(){

        abstract fun newsDao(): NewsDao

        companion object{
        @Volatile private var INSTANCE: NewsDB? =null

            fun getDatabase(context: Context): NewsDB {
                return INSTANCE ?: synchronized(this) {
                    val instance = databaseBuilder(
                        context.applicationContext,
                        NewsDB::class.java,
                        "news_database"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }

        }


}
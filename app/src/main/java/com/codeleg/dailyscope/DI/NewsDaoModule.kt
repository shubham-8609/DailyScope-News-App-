package com.codeleg.dailyscope.DI

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.codeleg.dailyscope.database.local.NewsDB
import com.codeleg.dailyscope.database.local.NewsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NewsDaoModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): NewsDB {
        return Room.databaseBuilder(
            application,
            NewsDB::class.java,
            "news_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(db: NewsDB): NewsDao {
        return db.newsDao()
    }
}
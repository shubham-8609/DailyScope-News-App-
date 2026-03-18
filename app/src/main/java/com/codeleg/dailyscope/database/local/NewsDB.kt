package com.codeleg.dailyscope.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ArticleEntity::class], version = 2, exportSchema = false)
@TypeConverters(Convertors::class)
abstract class NewsDB : RoomDatabase(){

        abstract fun newsDao(): NewsDao

        companion object{
            /*val MIGRATION_2_3 = object : Migration(2 , 3){
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE articles ADD COLUMN isNotified INTEGER DEFAULT 0 NOT NULL")
                }
            }*/
        @Volatile private var INSTANCE: NewsDB? =null

            fun getDatabase(context: Context): NewsDB {
                return INSTANCE ?: synchronized(this) {
                    val instance = databaseBuilder(
                        context.applicationContext,
                        NewsDB::class.java,
                        "news_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    instance
                }
            }

        }


}
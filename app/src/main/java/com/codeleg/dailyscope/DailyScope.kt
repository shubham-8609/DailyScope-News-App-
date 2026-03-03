package com.codeleg.dailyscope

import android.app.Application
import com.codeleg.dailyscope.database.local.NewsDB
import com.codeleg.dailyscope.database.local.NewsDao

class DailyScope : Application() {

    val newsDao: NewsDao by lazy {
        NewsDB.getDatabase(this).newsDao()
    }

}
package com.codeleg.dailyscope

import android.app.Application
import com.codeleg.dailyscope.DI.DaggerAppComponent
import com.codeleg.dailyscope.database.local.NewsDB
import com.codeleg.dailyscope.database.preference.settingsDataStore
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.database.repository.SettingsRepository
import com.codeleg.dailyscope.utils.NotificationChannel
import com.codeleg.dailyscope.worker.BackgroundSyncManager


class DailyScope : Application() {

    lateinit var newsRepository: NewsRepository
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        NotificationChannel.create(this)


        val appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        settingsRepository = appComponent.getSettingsRepository()
        newsRepository = appComponent.getNewsRepository()

        BackgroundSyncManager(this, settingsRepository).start()
    }
}
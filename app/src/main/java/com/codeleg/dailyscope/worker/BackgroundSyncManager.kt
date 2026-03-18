package com.codeleg.dailyscope.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.codeleg.dailyscope.database.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BackgroundSyncManager(
    private val context: Context,
    private val settingsRepo: SettingsRepository
) {

        val workManager = WorkManager.getInstance(context)
    fun start() {
        Log.d("codeleg", "Starting BackgroundSyncManager --BackgroundSyncManager")
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepo.backgroundSyncFlow
                .distinctUntilChanged()
                .collect { enabled ->
                    Log.d("codeleg" , "Collected backgroundSyncFlow value: $enabled -- BackgroundSyncManager")
                    handleBackgroundSync(enabled)
                }
        }
            Log.d("codeleg" , "Starting BreakingNewsWorker -- BackgroundSyncManager")
            startBreakingNewsWorker()
    }

    private fun handleBackgroundSync(enabled: Boolean) {

        if (enabled) {
            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(
                10, TimeUnit.HOURS
            ).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build()
            workManager.enqueueUniquePeriodicWork(
                "news_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.d("codeleg", "Enqueued NewsSyncWorker with background sync enabled. -- BackgroundSyncManager")
        } else {
            workManager.cancelUniqueWork("news_sync")
        }
    }

    fun startBreakingNewsWorker() {
        val request = PeriodicWorkRequestBuilder<BreakingNewsWorker>(
            16, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "breaking_news_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Log.d("codeleg", "Enqueued BreakingNewsWorker to run every 16 minutes. -- BackgroundSyncManager")
    }
}
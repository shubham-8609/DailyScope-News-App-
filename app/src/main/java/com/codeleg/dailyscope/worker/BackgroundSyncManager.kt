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
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepo.breakingNewsNotificationFlow
                .distinctUntilChanged()
                .collect { enabled ->
                    Log.d("codeleg", "Collected breakingNewsNotificationFlow value: $enabled -- BackgroundSyncManager")
                    handleBreakingNewsSync(enabled)
                }
        }
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepo.autoCleanupFlow
                .distinctUntilChanged()
                .collect { enabled ->
                    Log.d("codeleg", "Collected autoCleanupFlow value: $enabled -- BackgroundSyncManager")
                    handleAutoCleanup(enabled)
                }
        }
    }

    private fun handleBackgroundSync(enabled: Boolean) {

        if (enabled) {
            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(
                8, TimeUnit.HOURS
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

    private fun handleBreakingNewsSync(enabled: Boolean) {
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<BreakingNewsWorker>(
                3, TimeUnit.HOURS
            ).build()

            workManager.enqueueUniquePeriodicWork(
                "breaking_news_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.d("codeleg", "Enqueued BreakingNewsWorker with breaking news alerts enabled. -- BackgroundSyncManager")
        } else {
            workManager.cancelUniqueWork("breaking_news_sync")
            Log.d("codeleg", "Cancelled BreakingNewsWorker because breaking news alerts disabled. -- BackgroundSyncManager")
        }
    }

    private fun handleAutoCleanup(enabled: Boolean) {
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<CleanupOldArticlesWorker>(
                3, TimeUnit.DAYS
            ).build()
            workManager.enqueueUniquePeriodicWork(
                "cleanup_old_articles",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.d("codeleg", "Enqueued CleanupOldArticlesWorker with auto cleanup enabled. -- BackgroundSyncManager")
        } else {
            workManager.cancelUniqueWork("cleanup_old_articles")
            Log.d("codeleg", "Cancelled CleanupOldArticlesWorker because auto cleanup disabled. -- BackgroundSyncManager")
        }
    }
}
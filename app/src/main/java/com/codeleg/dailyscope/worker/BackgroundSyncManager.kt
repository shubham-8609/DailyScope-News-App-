package com.codeleg.dailyscope.worker

import android.content.Context
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

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepo.backgroundSyncFlow
                .distinctUntilChanged()
                .collect { enabled ->
                    handleBackgroundSync(enabled)
                }
        }
    }

    private fun handleBackgroundSync(enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)

        if (enabled) {
            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(
                15, TimeUnit.MINUTES
            ).setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build()

            workManager.enqueueUniquePeriodicWork(
                "news_sync",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        } else {
            workManager.cancelUniqueWork("news_sync")
        }
    }
}
package com.codeleg.dailyscope.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeleg.dailyscope.DailyScope
import java.time.LocalDate

class CleanupOldArticlesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as DailyScope
        val newsRepo = app.newsRepository

        return try {
            val cutoffDate = LocalDate.now().minusDays(4).toString()
            val deleted = newsRepo.deleteOldArticles(cutoffDate)
            Log.d(
                "codeleg",
                "CleanupOldArticlesWorker deleted $deleted articles older than $cutoffDate"
            )
            Result.success()
        } catch (e: Exception) {
            Log.e("codeleg", "CleanupOldArticlesWorker failed: ${e.message}")
            Result.failure()
        }
    }
}

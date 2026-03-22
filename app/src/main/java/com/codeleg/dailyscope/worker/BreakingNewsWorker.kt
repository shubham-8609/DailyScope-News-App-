package com.codeleg.dailyscope.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.database.local.toEntity
import com.codeleg.dailyscope.utils.NotificationHelper
import kotlinx.coroutines.flow.first

class BreakingNewsWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {

        try {
            val newsRepo = (context.applicationContext as DailyScope).newsRepository
            val settingsRepo = (context.applicationContext as DailyScope).settingsRepository

            if (!settingsRepo.breakingNewsNotificationFlow.first()) {
                Log.d("codeleg", "Breaking news notifications disabled. Skipping work. --BreakingNewsWorker")
                return Result.success()
            }

            if (settingsRepo.hasNotificationPermission(context)) {
                val isGood = settingsRepo.isGoodNews()
                val news = if (isGood) {
                    newsRepo.getGoodNews().ifEmpty { newsRepo.getBadNews() }
                } else {
                    newsRepo.getBadNews().ifEmpty { newsRepo.getGoodNews() }
                }
                if (news.isNotEmpty()) {
                    val notifiedIds = newsRepo.getNotifiedArticles().map { it.id }.toSet()
                    val pool = news.filterNot { notifiedIds.contains(it.id) }
                    if (pool.isEmpty()) return Result.success() // nothing new to notify
                    val article = pool.random()
                    newsRepo.updateArticles(article.toEntity().copy(isNotified = true))

                    Log.d(
                        "codeleg",
                        if (isGood) "Good news found sending notification " else "Bad news found sending notification "
                    )
                    Log.d("codeleg", "Article title: ${article.title} -- BreakingNewsWorker")
                    NotificationHelper.showBreakingNews(
                        context,
                        "Latest news : ${article.title}",
                        "Click to read more!",
                        article.image ?: "",
                        article
                    )
                }
                settingsRepo.setGoodNews(!isGood)
            } else {
                Log.d(
                    "codeleg",
                    "Notification permission not granted. Skipping process   --BreakingNewsWorker"
                )
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("codeleg", "Error in BreakingNewsWorker: ${e.message}")
            return Result.failure()
        }
    }


}
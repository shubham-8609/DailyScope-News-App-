package com.codeleg.dailyscope.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.utils.NotificationHelper
import kotlinx.coroutines.flow.first

class NewsSyncWorker(val context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context , workerParams) {
    val  newsRepo = (context.applicationContext as DailyScope).newsRepository
    val settingsRepo = (context.applicationContext as DailyScope).settingsRepository

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Log.d("codeleg", "NewsSyncWorker started  -- Thread: ${Thread.currentThread().name}")
        Log.d("codeleg" , "Started fetching news -- NewsSyncWorker")
        val fetchedNews  = newsRepo.refreshNews(country = "in", language = "en", headlinesOnly = false)
        if(settingsRepo.hasNotificationPermission(context)){
            Log.d("codeleg", "Notification permission check passed. Checking user preference for notifications. -- NewsSyncWorker")
            if(settingsRepo.notificationAllowedFlow.first()){
                Log.d("codeleg", "Notification permission granted by user. -- NewsSyncWorker")
                if(settingsRepo.fetchedNewsNotificationFlow.first()){
                    Log.d("codeleg" , "User has enabled fetchedNewes notifications. -- NewsSyncWorker")
                if(fetchedNews > 0){
                    Log.d("codeleg", "New articles found. Sending notification. -- NewsSyncWorker")
                    NotificationHelper.showNewFetchedNewsNotification(context , "See some latest news ", "See some of the latest news. $fetchedNews new news check it out!")
                } else {
                    Log.d("codeleg", "No new articles found.  Skipping notification. value of fetchedNews = $fetchedNews  -- NewsSyncWorker")
                }
                }else{
                    Log.d("codeleg", "User has not enabled fetched news notifications. Skipping notification. -- NewsSyncWorker")
                }
            }else{
                Log.d("codeleg", "User has not allowed notifications. Skipping notification. -- NewsSyncWorker")
            }
        }else{
            Log.d("codeleg", "Notification permission not granted. Skipping notification. -- NewsSyncWorker")
        }
        Log.d("codeleg", "NewsSyncWorker finished fetching news. Fetched ${fetchedNews} articles. -- NewsSyncWorker")
        return Result.success()
    }
}
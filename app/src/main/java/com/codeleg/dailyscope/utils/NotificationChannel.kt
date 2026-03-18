package com.codeleg.dailyscope.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannel {

    const val BREAKING_NEWS = "breaking_news"
    const val NEW_FETCHED_NEWS = "new_fetched_news"

    fun create(context: Context) {
        val breakingNewsChannel = NotificationChannel(
            BREAKING_NEWS,
            "Breaking News",
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifications for breaking news" }

        val newFetchedNewsChannel = NotificationChannel(
            NEW_FETCHED_NEWS,
            "New Fetched News",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Notifications for newly fetched news" }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(breakingNewsChannel)
        manager.createNotificationChannel(newFetchedNewsChannel)

    }


}
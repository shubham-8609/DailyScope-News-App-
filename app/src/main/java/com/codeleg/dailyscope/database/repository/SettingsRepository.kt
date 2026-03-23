package com.codeleg.dailyscope.database.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class SettingsRepository @Inject constructor(val dataStore: DataStore<Preferences>) {
    private val HEADLINES_ONLY = booleanPreferencesKey("headlines_only")
    private val AUTO_OPEN_SEARCH = booleanPreferencesKey("auto_open_search")
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val MATERIAL_YOU_ENABLE = booleanPreferencesKey("material_you_enable")
    private val AUTO_SPEAK = booleanPreferencesKey("auto_speak")
    private val ATTACHMENT = booleanPreferencesKey("attachment")
    private val NOTIFICATION_ALLOWED = booleanPreferencesKey("notifications_enabled")
    private val FETCHED_NEWS_NOTIFICATION = booleanPreferencesKey("fetched_news_notification")
    private val BREAKING_NEWS_NOTIFICATION = booleanPreferencesKey("breaking_news_notification")
    private val BACKGROUND_SYNC = booleanPreferencesKey("background_fetch")
    private val AUTO_CLEANUP = booleanPreferencesKey("auto_cleanup")
    private val IS_GOOD_NEWS = booleanPreferencesKey("is_good_news")
    val headlinesOnlyFlow: Flow<Boolean> = dataStore.data.map { it[HEADLINES_ONLY] ?: false }
    val autoOpenSearch: Flow<Boolean> = dataStore.data.map { it[AUTO_OPEN_SEARCH] ?: false }
    val autoSpeak: Flow<Boolean> = dataStore.data.map { it[AUTO_SPEAK] ?: false }
    val darkModeFlow: Flow<Boolean> = dataStore.data.map { it[DARK_MODE] ?: false }
    val materialYouFlow: Flow<Boolean> = dataStore.data.map { it[MATERIAL_YOU_ENABLE] ?: false }
    val attachmentFlow: Flow<Boolean> = dataStore.data.map { it[ATTACHMENT] ?: false }
    val notificationAllowedFlow: Flow<Boolean> = dataStore.data.map { it[NOTIFICATION_ALLOWED] ?: false }
    val fetchedNewsNotificationFlow: Flow<Boolean> = dataStore.data.map { it[FETCHED_NEWS_NOTIFICATION] ?: false }
    val breakingNewsNotificationFlow: Flow<Boolean> = dataStore.data.map { it[BREAKING_NEWS_NOTIFICATION] ?: true }
    val backgroundSyncFlow : Flow<Boolean> = dataStore.data.map{ it[BACKGROUND_SYNC] ?: false }
    val autoCleanupFlow: Flow<Boolean> = dataStore.data.map { it[AUTO_CLEANUP] ?: false }
//    val isGoodNewsFlow : Flow<Boolean> = dataStore.data.map { it[IS_GOOD_NEWS] ?: false }

    suspend fun isGoodNews(): Boolean {
        return dataStore.data.first()[IS_GOOD_NEWS] ?: false
    }

    suspend fun setGoodNews(value : Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_GOOD_NEWS] = value
        }
    }


    suspend fun setNotificationAllowed(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATION_ALLOWED] = enabled
        }
    }

    suspend fun setBreakingNewsNotification(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[BREAKING_NEWS_NOTIFICATION] = enabled
        }
    }

    suspend fun setAutoCleanup(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AUTO_CLEANUP] = enabled
        }
    }



    fun findCacheSize(cacheDir: File): String {
        val sizeInBytes = getFolderSize(cacheDir)
        return formatSize(sizeInBytes)
    }

    fun getFolderSize(dir: File?): Long {
        var size: Long = 0

        if (dir != null && dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getFolderSize(file)
                    } else {
                        file.length()
                    }
                }
            }
        }

        return size
    }

    fun formatSize(size: Long): String {

        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

package com.codeleg.dailyscope.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private val HEADLINES_ONLY = booleanPreferencesKey("headlines_only")
    private val AUTO_OPEN_SEARCH = booleanPreferencesKey("auto_open_search")
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val MATERIAL_YOU_ENABLE = booleanPreferencesKey("material_you_enable")
    private val AUTO_SPEAK = booleanPreferencesKey("auto_speak")
    private val ATTACHMENT = booleanPreferencesKey("attachment")
    val headlinesOnlyFlow: Flow<Boolean> = dataStore.data.map { it[HEADLINES_ONLY] ?: false }
    val autoOpenSearch: Flow<Boolean> = dataStore.data.map { it[AUTO_OPEN_SEARCH] ?: false }
    val autoSpeak: Flow<Boolean> = dataStore.data.map { it[AUTO_SPEAK] ?: false }
    val darkModeFlow: Flow<Boolean> = dataStore.data.map { it[DARK_MODE] ?: false }
    val materialYouFlow: Flow<Boolean> = dataStore.data.map { it[MATERIAL_YOU_ENABLE] ?: false }
    val attachmentFlow: Flow<Boolean> = dataStore.data.map { it[ATTACHMENT] ?: false }



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
}
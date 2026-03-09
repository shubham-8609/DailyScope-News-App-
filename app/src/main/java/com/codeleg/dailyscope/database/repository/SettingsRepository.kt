package com.codeleg.dailyscope.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private val HEADLINES_ONLY = booleanPreferencesKey("headlines_only")
    val headlinesOnlyFlow: Flow<Boolean> = dataStore.data.map { it[HEADLINES_ONLY] ?: false }



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
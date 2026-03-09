package com.codeleg.dailyscope.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val COUNTRY = stringPreferencesKey("country")
    private val LANGUAGE = stringPreferencesKey("language")
    private val HEADLINES_ONLY = booleanPreferencesKey("headlines_only")

    val countryFlow: Flow<String> =
        dataStore.data.map { it[COUNTRY] ?: "in" }

    val languageFlow: Flow<String> =
        dataStore.data.map { it[LANGUAGE] ?: "en" }

    val headlinesOnlyFlow: Flow<Boolean> =

        dataStore.data.map { it[HEADLINES_ONLY] ?: false }
}
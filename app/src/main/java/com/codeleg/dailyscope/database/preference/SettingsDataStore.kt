package com.codeleg.dailyscope.database.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

class SettingsDataStore(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataStore() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun putBoolean(key: String?, value: Boolean) {
        key ?: return
        scope.launch {
            dataStore.edit { it[booleanPreferencesKey(key)] = value }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[booleanPreferencesKey(key)] ?: defValue
        }
    }

    override fun putString(key: String?, value: String?) {
        key ?: return
        scope.launch {
            dataStore.edit { it[stringPreferencesKey(key)] = value ?: "" }
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[stringPreferencesKey(key)] ?: defValue
        }
    }

    override fun putInt(key: String?, value: Int) {
        key ?: return
        scope.launch {
            dataStore.edit { it[intPreferencesKey(key)] = value }
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[intPreferencesKey(key)] ?: defValue
        }
    }

    override fun putLong(key: String?, value: Long) {
        key ?: return
        scope.launch {
            dataStore.edit { it[longPreferencesKey(key)] = value }
        }
    }

    override fun getLong(key: String?, defValue: Long): Long {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[longPreferencesKey(key)] ?: defValue
        }
    }

    override fun putFloat(key: String?, value: Float) {
        key ?: return
        scope.launch {
            dataStore.edit { it[floatPreferencesKey(key)] = value }
        }
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[floatPreferencesKey(key)] ?: defValue
        }
    }

    override fun putStringSet(key: String?, values: Set<String>?) {
        key ?: return
        scope.launch {
            dataStore.edit { it[stringSetPreferencesKey(key)] = values ?: emptySet() }
        }
    }

    override fun getStringSet(key: String?, defValue: Set<String>?): Set<String>? {
        key ?: return defValue
        return runBlocking {
            dataStore.data.first()[stringSetPreferencesKey(key)] ?: defValue
        }
    }
}
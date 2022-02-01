package com.devdunnapps.amplify.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor (
    private val context: Context
): PreferencesRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "amplify_settings")

    override suspend fun write(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun writeBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun read(key: String): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }

    override suspend fun readBoolean(key: String): Boolean {
        return context.dataStore.data.first()[booleanPreferencesKey(key)] ?: true
    }
}

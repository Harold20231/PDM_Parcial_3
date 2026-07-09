package com.example.rankeuca.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ApiKeyManager(private val context: Context) {

    companion object {
        private val API_KEY = stringPreferencesKey("api_key")
        private val CARNET = stringPreferencesKey("carnet")
    }

    val apiKeyFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[API_KEY] }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    suspend fun saveCarnet(carnet: String) {
        context.dataStore.edit { preferences ->
            preferences[CARNET] = carnet
        }
    }

    suspend fun getApiKey(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[API_KEY] }
            .firstOrNull()
    }

    suspend fun clearApiKey() {
        context.dataStore.edit { preferences ->
            preferences.remove(API_KEY)
            preferences.remove(CARNET)
        }
    }
}
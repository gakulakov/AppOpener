package com.example.appopenerwidget.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val IS_COMPLETED_ONBOARDING = booleanPreferencesKey("is_completed_onboarding")

suspend fun some(context: Context) {
    context.dataStore.data.map { preferences ->
        preferences[IS_COMPLETED_ONBOARDING]
    }
}
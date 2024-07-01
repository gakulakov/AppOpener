package com.example.appopenerwidget.screens.onboarding

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appopenerwidget.data.datastore.IS_COMPLETED_ONBOARDING
import com.example.appopenerwidget.data.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    val isCompletedOnboarding: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_COMPLETED_ONBOARDING] ?: false
        }
    fun changeIsCompleted(isCompleted: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[IS_COMPLETED_ONBOARDING] = isCompleted
            }
        }
    }
}
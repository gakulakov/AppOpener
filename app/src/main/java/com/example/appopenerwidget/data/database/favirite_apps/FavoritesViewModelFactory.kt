package com.example.appopenerwidget.data.database.favirite_apps

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appopenerwidget.screens.main.MainViewModel

class FavoritesViewModelFactory(private val context: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
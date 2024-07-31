package com.gakulakov.appopenerwidget.data.database.favirite_apps

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : ViewModel() {
    private val favoritesDB = FavoritesDataBase.getInstance(application)
    private val favoritesDao = favoritesDB.dao
    val favorites: Flow<List<Favorite>> = favoritesDao.getAllFavoriteApps()

    fun addFavorite(app: Favorite) {
        viewModelScope.launch {
                favoritesDao.addFavoriteApp(app)
        }
    }

    fun removeFavorite(app: Favorite) {
        viewModelScope.launch {
            favoritesDao.removeFavoriteApp(app)
        }
    }

    fun clear() {
        viewModelScope.launch {
            favoritesDao.clearTable()
        }
    }

}
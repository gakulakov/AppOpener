package com.gakulakov.appopenerwidget.utils

import android.content.Context
import com.gakulakov.appopenerwidget.data.database.favirite_apps.Favorite
import com.gakulakov.appopenerwidget.data.database.favirite_apps.FavoritesDataBase
import kotlinx.coroutines.flow.firstOrNull

suspend fun getApplicationsFromDB(context: Context): List<Favorite> {
    val favoritesDB = FavoritesDataBase.getInstance(context)
    val favoritesDao = favoritesDB.dao.getAllFavoriteApps()
    return favoritesDao.firstOrNull() ?: emptyList()
}
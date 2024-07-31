package com.gakulakov.appopenerwidget.data.database.favirite_apps

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert
    suspend fun addFavoriteApp(app: Favorite)

    @Update
    suspend fun updateFavoriteApp(app: Favorite)

    @Delete
    suspend fun removeFavoriteApp(app: Favorite)

    @Query("SELECT * FROM favorites")
    fun getAllFavoriteApps(): Flow<List<Favorite>>

    @Query("DELETE FROM favorites")
    suspend fun clearTable()
}
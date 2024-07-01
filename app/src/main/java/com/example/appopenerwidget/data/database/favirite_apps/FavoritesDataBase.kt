package com.example.appopenerwidget.data.database.favirite_apps

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = 4)
abstract class FavoritesDataBase : RoomDatabase() {
    abstract val dao: FavoritesDao

    companion object {
        private var INSTANCE: FavoritesDataBase? = null
        fun getInstance(context: Context): FavoritesDataBase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FavoritesDataBase::class.java,
                        "favoritesdb"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
package com.example.appopenerwidget.data.database.favirite_apps

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey
    val appId: String,
//    val icon: Bitmap,
    val appName: String,
)

package com.example.appopenerwidget.data.database.favirite_apps

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey
    val appId: String,
    val appName: String,
)

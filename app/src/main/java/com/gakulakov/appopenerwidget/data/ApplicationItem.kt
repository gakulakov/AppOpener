package com.gakulakov.appopenerwidget.data

import android.graphics.Bitmap

data class ApplicationItem(
    val appId: String,
    val appName: String,
    val id: Long? = null,
    val icon: Bitmap? = null,
)

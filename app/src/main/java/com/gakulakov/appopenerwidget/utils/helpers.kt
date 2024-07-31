package com.gakulakov.appopenerwidget.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.Display
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator

fun maxLengthString(value: String): String {
    return if(value.length >= 20) String.format("%.20s...", value).trim() else value
}

fun consoleLog(value: String) {
    Log.d("MyLog", value)
}

fun getDeviceName(): String {
    val model = Build.MODEL
    return model
}

fun getCurrentDisplay(context: Context): Display? {
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val displays = displayManager.displays

    // Если ваше приложение работает на одном экране, возвращаем первый дисплей
    return displays.firstOrNull()
}

fun getWindowMetrics(context: Context): WindowMetrics {
    val windowMetricsCalculator = WindowMetricsCalculator.getOrCreate()
    return windowMetricsCalculator.computeCurrentWindowMetrics(context)
}
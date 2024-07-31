package com.gakulakov.appopenerwidget.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.gakulakov.appopenerwidget.data.ApplicationItem

fun getAllApplications(context: Context): List<ApplicationItem> {
    val applications = mutableListOf<ApplicationItem>()
    val mainIntent = Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    val pkgAppsList = context.packageManager.queryIntentActivities(mainIntent, 0)

    try {
        pkgAppsList.forEachIndexed { index, app ->
            applications.add(
                ApplicationItem(
                    appId = app.activityInfo.packageName,
                    appName = app.activityInfo.packageName,
                    id = index.toLong()
                )
            )
        }

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return applications
}
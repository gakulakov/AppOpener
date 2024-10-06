package com.gakulakov.appopenerwidget

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.gakulakov.appopenerwidget.utils.consoleLog


class WidgetClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (AppOpenerWidgetProvider.CLICK_ACTION == intent?.action) {
            val itemId = intent.getStringExtra(AppOpenerWidgetProvider.EXTRA_ITEM_ID)

            val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(workDataOf("application" to itemId))
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
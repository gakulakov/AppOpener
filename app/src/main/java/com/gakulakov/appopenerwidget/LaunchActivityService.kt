package com.gakulakov.appopenerwidget

import android.app.ActivityOptions
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gakulakov.appopenerwidget.utils.consoleLog

class MyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun doWork(): Result {
        val itemId = inputData.getString("application")

        val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("application", itemId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val uniqueRequestCode = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            uniqueRequestCode,
            activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ActivityOptions.makeBasic().apply {
                launchDisplayId = 1
                setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            }.toBundle()
        )

        try {
            pendingIntent.send(applicationContext, PendingIntent.FLAG_UPDATE_CURRENT, activityIntent)
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }

        return Result.success()
    }
}
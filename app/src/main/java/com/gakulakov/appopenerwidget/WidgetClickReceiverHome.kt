package com.gakulakov.appopenerwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gakulakov.appopenerwidget.utils.consoleLog

class WidgetClickHomeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (AppOpenerWidgetProvider.CLICK_ACTION == intent?.action) {
            val itemId = intent.getStringExtra(AppOpenerWidgetProvider.EXTRA_ITEM_ID)

            try {
                val activityIntentHome =
                    context.packageManager?.getLaunchIntentForPackage(itemId!!)
                        ?.apply {
                            putExtra("application", itemId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                context.startActivity(activityIntentHome)
            } catch (e: Exception) {
                consoleLog(e.toString())
            }
        }
    }
}
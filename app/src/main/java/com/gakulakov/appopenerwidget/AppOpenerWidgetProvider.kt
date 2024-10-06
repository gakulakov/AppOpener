package com.gakulakov.appopenerwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.RemoteViews
import com.gakulakov.appopenerwidget.utils.consoleLog

open class AppOpenerWidgetProvider(val isHome: Boolean? = false) : AppWidgetProvider() {
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {

        if (context != null) {
            for (appWidgetId in appWidgetIds!!) {

                val intent = Intent(context, WidgetRemoveViewsService::class.java)
                    .apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                    }

                val remoteViews =
                    RemoteViews(context.packageName, R.layout.widget_app_opener).apply {
                        setRemoteAdapter(R.id.widget_grid_view, intent)
                        setEmptyView(R.id.widget_grid_view, R.id.empty_view)
                    }


                // TODO: Можно отказаться от избыточного BroadcastReceiver (ЗАРЕФАКТОРИТЬ!)

                val clickIntent = Intent(context, if(isHome == true) WidgetClickHomeReceiver::class.java else WidgetClickReceiver::class.java).apply {
                    action = CLICK_ACTION
                }

                val clickPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                remoteViews.setPendingIntentTemplate(R.id.widget_grid_view, clickPendingIntent)

                val intentTab =
                    Intent(context, this::class.java).apply {
                        action = CHANGE_TAB
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }

                val pendingIntentAllAppsTab = PendingIntent.getBroadcast(
                    context,
                    1,
                    intentTab.apply {
                        putExtra("tab_name", "all_applications")
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                val pendingIntentFavoritesTab = PendingIntent.getBroadcast(
                    context,
                    2,
                    intentTab.apply {
                        putExtra("tab_name", "favorites")
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                remoteViews.setOnClickPendingIntent(
                    R.id.all_applications_tab,
                    pendingIntentAllAppsTab
                )
                remoteViews.setOnClickPendingIntent(R.id.favorites_tab, pendingIntentAllAppsTab)
                remoteViews.setOnClickPendingIntent(R.id.favorites_tab, pendingIntentFavoritesTab)


                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }


        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.wtf("MyLog", "UPDATE")
    }

    override fun onReceive(context: Context, intent: Intent) {

        consoleLog("CLICK")

        if (intent.action == CHANGE_TAB) {
            // TODO: Зарефакторить
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            val tabName = intent.getStringExtra("tab_name") ?: ""
            val views = RemoteViews(context.packageName, R.layout.widget_app_opener)

            if (tabName == "all_applications") {
                views.setTextViewTextSize(
                    R.id.all_applications_tab,
                    TypedValue.COMPLEX_UNIT_SP,
                    16f
                )
                views.setTextColor(R.id.all_applications_tab, Color.WHITE)
                views.setTextViewTextSize(R.id.favorites_tab, TypedValue.COMPLEX_UNIT_SP, 12f)
                views.setTextColor(R.id.favorites_tab, Color.GRAY)

                updateWidgetData(context, "all_applications")
            } else {
                views.setTextViewTextSize(
                    R.id.all_applications_tab,
                    TypedValue.COMPLEX_UNIT_SP,
                    12f
                )
                views.setTextColor(R.id.all_applications_tab, Color.GRAY)
                views.setTextViewTextSize(R.id.favorites_tab, TypedValue.COMPLEX_UNIT_SP, 16f)
                views.setTextColor(R.id.favorites_tab, Color.WHITE)

                consoleLog("FAVORITES")
                updateWidgetData(context, "favorites")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        if (intent.action == UPDATE_FAVORITES) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    this::class.java
                )
            )

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view)
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        Log.wtf("MyLog", "DELETED $appWidgetIds")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        Log.wtf("MyLog", "DISABLED")
    }

    private fun updateWidgetData(context: Context, state: String) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_tab", state).apply()

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                this::class.java
            )
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view)
    }

    companion object {
        const val CLICK_ACTION = "com.gakulakov.appopenerwidget.CLICK_ACTION"
        const val EXTRA_ITEM_ID = "com.gakulakov.appopenerwidget.EXTRA_ITEM_ID"
        const val EXTRA_IS_HOME = "com.gakulakov.appopenerwidget.IS_HOME"
        const val CHANGE_TAB = "com.gakulakov.appopenerwidget.CHANGE_TAB"
        const val UPDATE_FAVORITES = "com.gakulakov.appopenerwidget.UPDATE"
    }
}
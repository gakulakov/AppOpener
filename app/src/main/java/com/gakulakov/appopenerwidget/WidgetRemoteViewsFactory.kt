package com.gakulakov.appopenerwidget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.toBitmap
import com.gakulakov.appopenerwidget.data.ApplicationItem
import com.gakulakov.appopenerwidget.data.database.favirite_apps.Favorite
import com.gakulakov.appopenerwidget.utils.consoleLog
import com.gakulakov.appopenerwidget.utils.getAllApplications
import com.gakulakov.appopenerwidget.utils.getApplicationsFromDB
import kotlinx.coroutines.runBlocking

class WidgetRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {
    private var favorites: List<Favorite> = emptyList()
    private var allApplications: List<ApplicationItem> = emptyList()
    private var selectedTab = "all_applications"

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        consoleLog("UPDATE")

        updateSelectedTab()
        runBlocking {
            favorites = getApplicationsFromDB(context)
            allApplications = getAllApplications(context)
        }

    }

    override fun onDestroy() {
    }

    override fun getCount(): Int =
        if (selectedTab == "favorites") favorites.size else allApplications.size

    override fun getViewAt(position: Int): RemoteViews {
        // TODO: Переделать на нормальную логику изменения
        val remoteView = RemoteViews(context.packageName, R.layout.widget_list_item)
        if (selectedTab == "favorites") {
            val item = favorites[position]

            val appIcon = context.packageManager.getApplicationIcon(item.appId).toBitmap(
                width = 128,
                height = 128,
                config = Bitmap.Config.ARGB_8888
            )

            remoteView.setImageViewBitmap(R.id.list_item_image, appIcon)

            val fillInIntent = Intent(
                context,
                AppOpenerWidgetProvider::class.java
            ).apply {
                action = AppOpenerWidgetProvider.CLICK_ACTION
                putExtra(AppOpenerWidgetProvider.EXTRA_ITEM_ID, item.appId)
            }
            remoteView.setOnClickFillInIntent(R.id.list_item_image, fillInIntent)

        } else {
            val item = allApplications[position]

            val appIcon = context.packageManager.getApplicationIcon(item.appId).toBitmap(
                width = 128,
                height = 128,
                config = Bitmap.Config.ARGB_8888
            )

            remoteView.setImageViewBitmap(R.id.list_item_image, appIcon)

            val fillInIntent = Intent(
                context,
                AppOpenerWidgetProvider::class.java
            ).apply {
                action = AppOpenerWidgetProvider.CLICK_ACTION
                putExtra(AppOpenerWidgetProvider.EXTRA_ITEM_ID, item.appId)
            }
            remoteView.setOnClickFillInIntent(R.id.list_item_image, fillInIntent)
        }



        return remoteView
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun updateSelectedTab() {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val state = prefs.getString("selected_tab", "all_applications") ?: "all_applications"

        selectedTab = state
    }

}
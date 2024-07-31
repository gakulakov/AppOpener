package com.gakulakov.appopenerwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toIcon
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.gakulakov.appopenerwidget.data.ApplicationItem
import com.gakulakov.appopenerwidget.data.database.favirite_apps.Favorite
import com.gakulakov.appopenerwidget.utils.getApplicationsFromDB


val ActionParam = ActionParameters
    .Key<String>("application")

object OpenerAppWidget : GlanceAppWidget() {
    val selectedTabKey = stringPreferencesKey("selectedTab")
    val actionParameterTabKey = ActionParameters.Key<String>("action_tab_parameter")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val applications = getAllApps(context)
        val favorites = getApplicationsFromDB(context)

        provideContent {
            Content(context, applications, favorites)
        }
    }

    private fun getAllApps(context: Context): List<ApplicationItem> {
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


    @SuppressLint("RestrictedApi")
    @Composable
    private fun Content(
        context: Context,
        applications: List<ApplicationItem>,
        favorites: List<Favorite>
    ) {
        val selectedTab =
            currentState(key = selectedTabKey) ?: context.getString(R.string.all_applications)

        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = context.getString(R.string.all_applications),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = if (selectedTab == context.getString(R.string.all_applications)) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight().padding(vertical = 12.dp)
                        .clickable(
                            actionRunCallback<ChangeTabActionCallback>(
                                parameters = actionParametersOf(
                                    actionParameterTabKey to context.getString(
                                        R.string.all_applications
                                    )
                                )
                            )
                        )
                )
                Text(
                    text = context.getString(R.string.favorites),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = if (selectedTab == context.getString(R.string.favorites)) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight().padding(vertical = 12.dp)
                        .clickable(
                            actionRunCallback<ChangeTabActionCallback>(
                                parameters = actionParametersOf(
                                    actionParameterTabKey to context.getString(
                                        R.string.favorites
                                    )
                                )
                            )
                        )
                )
            }
            if (selectedTab == context.getString(R.string.all_applications)) {
                LazyVerticalGrid(
                    gridCells = GridCells.Fixed(4),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(items = applications) {
                        Column {
                            val appIcon =
                                context.packageManager.getApplicationIcon(it.appName)
                                    .toBitmap(
                                        width = 64,
                                        height = 64,
                                        config = Bitmap.Config.ARGB_8888
                                    ).toIcon()

                            Image(
                                provider = ImageProvider(appIcon),
                                contentDescription = it.appId,
                                modifier = GlanceModifier
                                    .size(64.dp)
                                    .clickable(
                                        onClick = actionStartActivity<MainActivity>(
                                            actionParametersOf(ActionParam to it.appId)
                                        )
                                    )
                            )

                            Spacer(modifier = GlanceModifier.height(14.dp))
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    gridCells = GridCells.Fixed(4),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(items = favorites) {
                        Column(modifier = GlanceModifier.size(64.dp)) {
                            val appIcon = context
                                .packageManager
                                .getApplicationIcon(it.appId)
                                .toBitmap(
                                    width = 64,
                                    height = 64,
                                    config = Bitmap.Config.ARGB_8888
                                ).toIcon()
                            Image(
                                provider = ImageProvider(appIcon),
                                contentDescription = it.appId,
                                modifier = GlanceModifier
                                    .size(64.dp)
                                    .clickable(
                                        onClick = actionStartActivity<MainActivity>(
                                            actionParametersOf(ActionParam to it.appId)
                                        )
                                    )
                            )

                            Spacer(modifier = GlanceModifier.height(14.dp))
                        }
                    }

                    item {
                        Image(
                            provider = ImageProvider(R.drawable.ic_refresh),
                            contentDescription = null,
                            modifier = GlanceModifier.size(64.dp)
                                .clickable(actionRunCallback<RefreshActionCallback>())
                        )
                    }
                }
            }
        }

    }
}


class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = OpenerAppWidget
}

class ChangeTabActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val selectedTab = parameters[OpenerAppWidget.actionParameterTabKey]
                ?: context.getString(R.string.all_applications)
            prefs[OpenerAppWidget.selectedTabKey] = selectedTab
        }
        OpenerAppWidget.update(context, glanceId)
    }
}

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        OpenerAppWidget.update(context, glanceId)
    }
}
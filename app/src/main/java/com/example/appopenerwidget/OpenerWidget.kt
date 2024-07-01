package com.example.appopenerwidget

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.BitmapImageProvider
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.media3.common.util.UnstableApi
import com.example.appopenerwidget.data.ApplicationItem
import kotlinx.coroutines.launch


class OpenerWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TODO("Create GlanceAppWidget")
}

val ActionParam = ActionParameters
    .Key<String>("application")

class OpenerAppWidget : GlanceAppWidget() {
    val activityOptions = ActivityOptions.makeBasic().apply {
        launchDisplayId = 1
    }.toBundle()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent()
        }
    }


    @OptIn(UnstableApi::class)
    @SuppressLint("RestrictedApi")
    @Composable
    private fun MyContent() {
        val mContext = LocalContext.current

        val coroutineScope = rememberCoroutineScope()

        val applications = remember { mutableStateListOf<ApplicationItem>() }
        var isLoading by remember {
            mutableStateOf(false)
        }
        var selectedTab by remember {
            mutableStateOf("Все приложения")
        }

        DisposableEffect(Unit) {
            isLoading = true
            coroutineScope.launch {
                val mainIntent = Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                val pkgAppsList = mContext.packageManager.queryIntentActivities(mainIntent, 0)

                try {
                    pkgAppsList.forEach {
                        val appIcon =
                            mContext.packageManager.getApplicationIcon(it.activityInfo.packageName)
                                .toBitmap(
                                    width = 128,
                                    height = 128,
                                    config = Bitmap.Config.ARGB_8888
                                )

                        applications.add(
                            ApplicationItem(
                                appId = it.activityInfo.packageName,
                                icon = appIcon,
                                appName = ""
                            )
                        )
                    }
                    isLoading = false
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }


            onDispose {
                applications.clear()
            }
        }

        fun openAppById(applicationId: String) {
            val launchIntent =
                mContext.packageManager.getLaunchIntentForPackage(applicationId)
            if (launchIntent != null) {
                val pIntent = PendingIntent.getActivity(
                    mContext,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE,
                    activityOptions
                )
                pIntent.send(mContext, 0, launchIntent)
            }
        }

        Column {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    Text(
                        text = "Все приложения",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontWeight = if (selectedTab === "Все приложения") FontWeight.Bold else FontWeight.Medium
                        ),
                        modifier = GlanceModifier.defaultWeight().padding(vertical = 12.dp)
                            .clickable {
                                selectedTab = "Все приложения"
                            }
                    )
                    Text(
                        text = "Избранное",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontWeight = if (selectedTab === "Избранное") FontWeight.Bold else FontWeight.Medium
                        ),
                        modifier = GlanceModifier.defaultWeight().padding(vertical = 12.dp)
                            .clickable {
                                selectedTab = "Избранное"
                            }
                    )
                }
                if (selectedTab === "Все приложения") {
                    LazyVerticalGrid(
                        gridCells = GridCells.Fixed(4),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(items = applications,) {
                            Column {
                                Image(
                                    provider = BitmapImageProvider(it.icon),
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
                }

            }
        }

    }
}

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {

    // Let MyAppWidgetReceiver know which GlanceAppWidget to use
    override val glanceAppWidget: GlanceAppWidget = OpenerAppWidget()
}
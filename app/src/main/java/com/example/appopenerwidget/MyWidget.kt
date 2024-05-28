package com.example.appopenerwidget

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.media3.common.util.UnstableApi
import com.example.appopenerwidget.data.ApplicationItem


class MyWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TODO("Create GlanceAppWidget")
}

val ActionParam = ActionParameters
    .Key<String>("application")

class MyAppWidget : GlanceAppWidget() {
    val activityOptions = ActivityOptions.makeBasic().apply {
        launchDisplayId = 1
    }.toBundle()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent()
        }
    }



    @OptIn(UnstableApi::class) @SuppressLint("RestrictedApi")
    @Composable
    private fun MyContent() {
        val mContext = LocalContext.current

        val applications = remember { mutableStateListOf<ApplicationItem>() }

        LaunchedEffect(Unit) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            val pkgAppsList = mContext.packageManager.queryIntentActivities(mainIntent, 0)

            try {
                pkgAppsList.forEach {
                    val appIcon =
                        mContext.packageManager.getApplicationIcon(it.activityInfo.packageName)
                            .toBitmap(width = 128, height = 128, config = Bitmap.Config.ARGB_8888)

                    applications.add(
                        ApplicationItem(
                            appId = it.activityInfo.packageName,
                            icon = appIcon,
                            appName = ""
                        )
                    )
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
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



        LazyVerticalGrid(
            gridCells = GridCells.Fixed(4),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(items = applications) {
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

class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {

    // Let MyAppWidgetReceiver know which GlanceAppWidget to use
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}
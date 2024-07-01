package com.example.appopenerwidget.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.glance.LocalContext
import java.lang.ProcessBuilder.Redirect

@Composable
fun Redirect() {
    // TODO: Возможно не нужен
    val mContext = LocalContext.current as Activity
    val applicationId = mContext.intent.extras?.getString("application")
    LaunchedEffect(Unit) {
        if (applicationId != null) {
            openAppById(applicationId, mContext)
        }
    }

    if (applicationId != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

fun openAppById(applicationId: String, context: Activity) {
    val launchIntent =
        context.packageManager.getLaunchIntentForPackage(applicationId)
    context.startActivity(launchIntent)
    context.finish()
}
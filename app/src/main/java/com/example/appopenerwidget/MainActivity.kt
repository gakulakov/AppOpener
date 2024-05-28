package com.example.appopenerwidget

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.appopenerwidget.data.ApplicationItem
import com.example.appopenerwidget.ui.components.Main
import com.example.appopenerwidget.ui.theme.AppOpenerWidgetTheme
import java.security.AccessController.getContext


class MainActivity : ComponentActivity() {
    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val applicationId = intent.extras?.getString("application")

        setContent {
            AppOpenerWidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(Unit) {
                        if (applicationId != null) {
                            openAppById(applicationId)
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
                    } else {
                        Main()
                    }
                }
            }
        }
    }

    private fun openAppById(applicationId: String) {
        val launchIntent =
            this.packageManager.getLaunchIntentForPackage(applicationId)
        Log.d("MyLog", launchIntent.toString())
        this.startActivity(launchIntent)
        this.finish()
    }
}
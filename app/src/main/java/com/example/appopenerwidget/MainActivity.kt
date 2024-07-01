package com.example.appopenerwidget

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.appopenerwidget.data.Screen
import com.example.appopenerwidget.data.database.favirite_apps.FavoritesViewModel
import com.example.appopenerwidget.navigation.Navigation
import com.example.appopenerwidget.screens.onboarding.OnboardingViewModel
import com.example.appopenerwidget.ui.theme.AppOpenerWidgetTheme
import kotlinx.coroutines.launch


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
                    val onboardingViewModel: OnboardingViewModel by viewModels()
                    val isCompletedOnboarding by onboardingViewModel.isCompletedOnboarding.collectAsState(
                        initial = null
                    )

                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        if (applicationId != null) {
                            coroutineScope.launch {
                                openAppById(applicationId)
                            }
                        }
                    }

                    if (applicationId != null || isCompletedOnboarding === null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {

                        Navigation(
                            startDestination = if (isCompletedOnboarding as Boolean) Screen.Main.title else Screen.Onboarding.title
                        )
                    }

                }
            }
        }
    }

    private fun openAppById(applicationId: String) {
        val launchIntent =
            this.packageManager.getLaunchIntentForPackage(applicationId)
        this.startActivity(launchIntent)
        this.finish()
    }
}
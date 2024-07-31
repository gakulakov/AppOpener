package com.gakulakov.appopenerwidget

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import androidx.core.content.ContextCompat.getSystemService
import com.gakulakov.appopenerwidget.data.Screen
import com.gakulakov.appopenerwidget.navigation.Navigation
import com.gakulakov.appopenerwidget.screens.onboarding.OnboardingViewModel
import com.gakulakov.appopenerwidget.ui.theme.AppOpenerWidgetTheme
import com.gakulakov.appopenerwidget.utils.consoleLog
import com.gakulakov.appopenerwidget.utils.getWindowMetrics
import kotlinx.coroutines.launch
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.WindowMetrics
import kotlinx.coroutines.delay


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

                    val displays = (applicationContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager).displays
                    consoleLog(displays[0].toString())


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
        intent.removeExtra("application")
        this.startActivity(launchIntent)
        this.finish()
    }
}
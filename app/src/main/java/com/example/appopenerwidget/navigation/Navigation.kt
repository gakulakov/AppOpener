package com.example.appopenerwidget.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appopenerwidget.screens.onboarding.OnboardingViewModel
import com.example.appopenerwidget.data.Screen
import com.example.appopenerwidget.screens.main.Main
import com.example.appopenerwidget.screens.onboarding.Onboarding
import com.example.appopenerwidget.screens.Redirect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Navigation(startDestination: String = Screen.Onboarding.title, viewModel: OnboardingViewModel = viewModel()) {
    val isCompletedOnboarding by viewModel.isCompletedOnboarding.collectAsState(initial = null)
    val navController = rememberNavController()

    LaunchedEffect(isCompletedOnboarding) {
        isCompletedOnboarding?.let {
            if (!it) {
                navController.navigate(Screen.Onboarding.title)
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.title) {
            Onboarding(onFinish = { navController.navigate(Screen.Main.title) })
        }

        composable(Screen.Main.title) {
            Main()
        }

        composable(Screen.Redirect.title) {
            Redirect()
        }
    }
}
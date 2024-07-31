package com.gakulakov.appopenerwidget.screens.main

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gakulakov.appopenerwidget.data.ApplicationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _context = WeakReference(application.applicationContext)

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _applications = MutableStateFlow<MutableList<ApplicationItem>>(mutableListOf())
    val applications: StateFlow<MutableList<ApplicationItem>> = _applications.asStateFlow()

    fun getApplicationsOnDevice() {
        val mContext = _context.get()

        mContext?.let { context ->
            _isLoading.value = true
            viewModelScope.launch {
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

                val pkgAppsList = withContext(Dispatchers.IO) {
                    context.packageManager.queryIntentActivities(mainIntent, 0)
                }

                val applicationsList = mutableListOf<ApplicationItem>()

                pkgAppsList.map { activityInfo ->
                    async(Dispatchers.IO) {
                        try {
                            val appIcon =
                                context.packageManager.getApplicationIcon(activityInfo.activityInfo.packageName)
                                    .toBitmap(
                                        width = 128,
                                        height = 128,
                                        config = Bitmap.Config.ARGB_8888
                                    )

                            val appInfo = context.packageManager.getApplicationInfo(
                                activityInfo.activityInfo.packageName,
                                PackageManager.GET_META_DATA
                            )
                            val appName =
                                context.packageManager.getApplicationLabel(appInfo).toString()

                            ApplicationItem(
                                appId = activityInfo.activityInfo.packageName,
                                icon = appIcon,
                                appName = appName,
                            )
                        } catch (e: PackageManager.NameNotFoundException) {
                            e.printStackTrace()
                            null
                        }
                    }
                }.awaitAll().filterNotNull().distinctBy { it.appId }.forEach { applicationItem ->
                    applicationsList.add(applicationItem)
                }

                withContext(Dispatchers.Main) {
                    _applications.value.addAll(applicationsList)
                }
                _isLoading.value = false
            }
        }

    }

    fun changeSearchText(value: String) {
        _searchText.value = value
    }
}
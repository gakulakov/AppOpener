package com.example.appopenerwidget.ui.components

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.appopenerwidget.ActionParam
import com.example.appopenerwidget.MainActivity
import com.example.appopenerwidget.data.ApplicationItem
import com.example.appopenerwidget.utils.maxLengthString
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Main() {
    val applications = remember { mutableStateListOf<ApplicationItem>() }
    val mContext = LocalContext.current as Activity

    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        isLoading = true
        coroutineScope.launch {
            val mainIntent = Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            val pkgAppsList = mContext.packageManager.queryIntentActivities(mainIntent, 0)

            try {
                pkgAppsList.forEach {
                    val appIcon =
                        mContext.packageManager.getApplicationIcon(it.activityInfo.packageName)
                            .toBitmap(width = 128, height = 128, config = Bitmap.Config.ARGB_8888)

                    val appInfo = mContext.packageManager.getApplicationInfo(it.activityInfo.packageName, PackageManager.GET_META_DATA)
                    val appName = mContext.packageManager.getApplicationLabel(appInfo).toString()
                    applications.add(
                        ApplicationItem(
                            appId = it.activityInfo.packageName,
                            icon = appIcon,
                            appName = appName
                        )
                    )
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }

        }
    }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Button(onClick = {
            mContext.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        }) {
            Text(text = "Change Direction")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }),
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                Text(text = "Поиск...")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (!isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(64.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                state = gridState
            ) {
                items(items = applications.filter {
                          it.appName.contains(searchText, ignoreCase = true)
                }) { app ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(bitmap = app.icon.asImageBitmap(), contentDescription = app.appId, Modifier.size(64.dp))
                        Text(text = app.appName, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                CircularProgressIndicator()
            }
        }
    }
}
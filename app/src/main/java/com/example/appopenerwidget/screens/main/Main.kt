package com.example.appopenerwidget.screens.main

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appopenerwidget.R
import com.example.appopenerwidget.data.ApplicationItem
import com.example.appopenerwidget.data.database.favirite_apps.Favorite
import com.example.appopenerwidget.data.database.favirite_apps.FavoritesViewModel
import com.example.appopenerwidget.data.database.favirite_apps.FavoritesViewModelFactory
import com.example.appopenerwidget.ui.components.ApplicationCell
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Main() {
    val mContext = LocalContext.current.applicationContext as Application
    val scope = rememberCoroutineScope()

    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(mContext))

    val favoritesViewModel: FavoritesViewModel =
        viewModel(factory = FavoritesViewModelFactory(mContext))
    val favorites by favoritesViewModel.favorites.collectAsState(initial = emptyList())

    fun toggleFavoriteApp(app: Favorite) {
        val isAvailableFavoriteApp = favorites.any { it.appId == app.appId }
        if (isAvailableFavoriteApp) {
            favoritesViewModel.removeFavorite(app)
        } else {
            favoritesViewModel.addFavorite(app)
        }
    }

    val applications by mainViewModel.applications.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()


    LaunchedEffect(Unit) {
        mainViewModel.getApplicationsOnDevice()
    }

    val tabs = listOf("Все приложения", "Избранные")

    val pagerState = rememberPagerState(pageCount = {
        tabs.size
    })


    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .clip(
                    shape = RoundedCornerShape(26)
                )
                .border(1.dp, Color(234,238,242), shape = RoundedCornerShape(26))

            ,
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(234,238,242),
            indicator = { tabPositions ->
                SecondaryIndicator(
                    color = Color.Transparent,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->

                val color = remember {
                    Animatable(Color.LightGray)
                }

                val shadowOffset: Int by animateIntAsState(if (pagerState.currentPage == index) 6 else 0,
                    label = ""
                )

                LaunchedEffect(pagerState.currentPage) {
                    scope.launch {
                        if(pagerState.currentPage == index) {
                            color.animateTo(Color.White, tween(300))
                        } else {
                            color.animateTo(Color(234,238,242), tween(300))
                        }
                    }
                }

                Tab(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(shadowOffset.dp)
                        .clip(
                            shape = RoundedCornerShape(26)
                        )
                        .background(color.value)
                    ,
                    selected = pagerState.currentPage == index,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Black,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                ) {
                    Text(text = tabTitle)
                }
            }
        }

        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> AllApplicationsTab(
                    isLoading = isLoading,
                    applications = applications,
                    favorites = favorites,
                    onPressItem = { app ->
                        toggleFavoriteApp(Favorite(appId = app.appId, appName = app.appName))
                    }
                )
                1 -> FavoriteTab(applications = favorites)
            }
        }
    }
}

@Composable
fun FavoriteTab(applications: List<Favorite>) {
    val gridState = rememberLazyGridState()
    val mContext = LocalContext.current

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        columns = GridCells.Adaptive(64.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = gridState
    ) {
        items(
            items = applications,
            key = { item ->
                item.appId + applications.indexOfFirst { it.appId === item.appId }
                    .toString()
            }) { app ->
            val appIcon =
                mContext.packageManager.getApplicationIcon(app.appId)
                    .toBitmap(
                        width = 128,
                        height = 128,
                        config = Bitmap.Config.ARGB_8888
                    )

            ApplicationCell(title = app.appName, iconBitmap = appIcon.asImageBitmap(), onPress = {})

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllApplicationsTab(
    isLoading: Boolean,
    applications: List<ApplicationItem>,
    favorites: List<Favorite>,
    onPressItem: (app: ApplicationItem) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var searchText by remember { mutableStateOf("") }

    val gridState = rememberLazyGridState()

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
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
                Text(text = stringResource(id = R.string.placeholder_searchbar))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedPlaceholderColor = Color.LightGray,
                focusedPlaceholderColor = Color.LightGray,
                containerColor = Color.White,
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )
        if (!isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(64.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                state = gridState
            ) {
                items(
                    items = applications.filter {
                        it.appName.contains(searchText, ignoreCase = true)
                    },
                    key = { item ->
                        item.appId + applications.indexOfFirst { it.appId === item.appId }
                            .toString()
                    }) { app ->
                    val isFavorite = favorites.any { it.appId == app.appId }

                    ApplicationCell(
                        title = app.appName,
                        iconBitmap = app.icon.asImageBitmap(),
                        onPress = {
                            onPressItem(app)
                        },
                    )

                    AnimatedVisibility(
                        enter = fadeIn() + expandIn(),
                        exit = fadeOut() + shrinkOut(),
                        visible = isFavorite
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.Red,
                            modifier = Modifier.offset(20.dp)
                        )
                    }

                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
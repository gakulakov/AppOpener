package com.example.appopenerwidget.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.appopenerwidget.R
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Onboarding(onFinish: () -> Unit, viewModel: OnboardingViewModel = viewModel()) {
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val coroutineScope = rememberCoroutineScope()

    BackHandler(true) {
        // Or do nothing
        onFinish()
    }

    Box(
        modifier = Modifier
            .zIndex(1f)
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = {
                onFinish()
                viewModel.changeIsCompleted(true)
            },
            shape = RoundedCornerShape(6.dp),
            containerColor = Color.White,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> FirstPage()
                1 -> SecondPage()
                2 -> LastPage()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage == 2) {
                        onFinish()
                        viewModel.changeIsCompleted(true)
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) stringResource(id = R.string.done) else stringResource(
                        id = R.string.next_btn_onboarding
                    )
                )
            }
        }

    }
}

@Composable
fun FirstPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight(300))) {
                append(stringResource(id = R.string.welcome_onboarding) + "\n")
            }
            withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight(700))) {
                append(stringResource(id = R.string.app_name))
            }
        }, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), lineHeight = 36.sp)
    }

}

@Composable
fun SecondPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Просто добавьте наше приложение в виджеты на крышке вашего Samsung Galaxy Z Flip",
            textAlign = TextAlign.Center,
            modifier = Modifier.width(328.dp)
        )
    }
}

@Composable
fun LastPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Вот и всё", modifier = Modifier.padding(bottom = 38.dp))
        Text(
            text = "Ваше устройство может запускать любые приложения без ограничений на крышке Вашего смартфона",
            textAlign = TextAlign.Center,
        )
    }
}
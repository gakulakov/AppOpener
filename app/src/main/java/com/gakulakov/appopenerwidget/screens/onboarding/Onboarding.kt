package com.gakulakov.appopenerwidget.screens.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gakulakov.appopenerwidget.R
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Onboarding(onFinish: () -> Unit, viewModel: OnboardingViewModel = viewModel()) {
    val pagerState = rememberPagerState(pageCount = {
        4
    })
    val coroutineScope = rememberCoroutineScope()

    BackHandler(true) {
        viewModel.changeIsCompleted(true)
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
                2 -> ThirdPage()
                3 -> LastPage()
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
                    if (pagerState.currentPage == 3) {
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
                    text = if (pagerState.currentPage == 3) stringResource(id = R.string.done) else stringResource(
                        id = R.string.next_btn_onboarding
                    )
                )
            }
        }

    }
}

// TODO: Зарефакторить этот хардкод

@Composable
fun Title(isWelcome: Boolean = false, title: String) {
    Text(buildAnnotatedString {
        if (isWelcome) {
            withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight(300))) {
                append(stringResource(id = R.string.welcome_onboarding) + "\n")
            }
        }
        withStyle(style = SpanStyle(fontSize = 32.sp, fontWeight = FontWeight(700))) {
            append(title)
        }
    }, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), lineHeight = 36.sp)
}

@Composable
fun FirstPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.mockup_flip), contentDescription = "mockup image",
            modifier = Modifier
                .height(180.dp)
                .width(154.dp)
                .padding(bottom = 32.dp),
        )
        Title(true, stringResource(id = R.string.app_name))
    }

}

@Composable
fun SecondPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(title = stringResource(id = R.string.widget))
        Image(
            painter = painterResource(R.drawable.second_onboard), contentDescription = "mockup image",
            modifier = Modifier
                .height(180.dp)
                .width(300.dp)
                .padding(bottom = 32.dp),
        )
        Text(
            text = stringResource(id = R.string.onboarding_second),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(328.dp)
        )
    }
}

@Composable
fun ThirdPage() {
    val mContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 72.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(title = stringResource(id = R.string.settings))

        Image(
            painter = painterResource(R.drawable.settings), contentDescription = "mockup image",
            modifier = Modifier
                .height(180.dp)
                .width(300.dp)
                .padding(bottom = 32.dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_third),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(328.dp)
            )

            Button(onClick = {
                val intent = Intent("com.samsung.settings.SubScreenWidgetSettings")

                if (intent.resolveActivity(mContext.packageManager) != null) {
                    mContext.startActivity(intent)
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
                Text(text = stringResource(id = R.string.open_settings))
            }
        }

    }
}

@Composable
fun LastPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 72.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Title(title = stringResource(id = R.string.thats_all))

        Image(
            painter = painterResource(R.drawable.done), contentDescription = "mockup image",
            modifier = Modifier
                .height(200.dp)
                .width(192.dp)
                .padding(bottom = 32.dp),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally,) {
            Text(
                text = stringResource(id = R.string.onboarding_final),
                textAlign = TextAlign.Center,
            )
        }
    }
}
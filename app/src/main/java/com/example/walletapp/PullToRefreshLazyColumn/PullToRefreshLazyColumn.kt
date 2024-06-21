package com.example.walletapp.PullToRefreshLazyColumn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.walletapp.R
import kotlinx.coroutines.launch


//Не работает
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    content: @Composable () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
){
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ){
        content()

        if(pullToRefreshState.isRefreshing){
            LaunchedEffect(key1 = true) {
                onRefresh()
            }
        }

        LaunchedEffect(key1 = isRefreshing) {
            if(isRefreshing){
                pullToRefreshState.startRefresh()
            }else{
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
        )

    }

}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun CustomSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = RefreshState.IDLE)
    val scope = rememberCoroutineScope()
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val animatedOffset by animateFloatAsState(
        targetValue = if (swipeableState.currentValue == RefreshState.REFRESHING) 60f else 0f,
        label = ""
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    0f to RefreshState.IDLE,
                    100f to RefreshState.REFRESHING
                ),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
    ) {
        content()

        if (swipeableState.currentValue == RefreshState.REFRESHING && !isRefreshing) {
            onRefresh()
        }

        LaunchedEffect(swipeableState.currentValue) {
            if (swipeableState.currentValue == RefreshState.REFRESHING) {
                kotlinx.coroutines.delay(1500)
                scope.launch {
                    swipeableState.animateTo(RefreshState.IDLE)
                }
            }
        }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedOffset.dp)
                    .background(color = Color.Transparent)
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (swipeableState.currentValue == RefreshState.REFRESHING) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Canvas(modifier = Modifier.size(48.dp).rotate(rotation)) {
                            drawArc(
                                color = Color(0xFFBB2649),
                                startAngle = 0f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.safina_rgb_circle),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }

                }
            }
    }
}

private enum class RefreshState {
    IDLE,
    REFRESHING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshWithCustomIndicator(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val animatedOffset by animateFloatAsState(
        targetValue = if (pullToRefreshState.isRefreshing) 60f else 0f,
        label = ""
    )

    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = animatedOffset.dp)
        ) {
            content()
        }

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(key1 = true) {
                onRefresh()
            }
        }

        LaunchedEffect(key1 = isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedOffset.dp)
                .background(color = Color.Transparent)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (pullToRefreshState.isRefreshing) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(48.dp)
                ) {
                    Canvas(modifier = Modifier.size(48.dp).aspectRatio(1f).rotate(rotation)) {
                        drawArc(
                            color = Color(0xFFBB2649),
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.safina_rgb_circle),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

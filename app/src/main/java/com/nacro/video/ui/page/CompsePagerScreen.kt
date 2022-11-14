package com.nacro.video.ui.page

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.accompanist.pager.*
import com.nacro.video.model.domain.VideoData
import kotlinx.coroutines.flow.*
import kotlin.math.absoluteValue
import kotlin.math.sign

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PurePagerFromComposeUI(videos: List<VideoData>) {
    val pagerState = rememberPagerState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        count = videos.size,
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
        itemSpacing = 2.dp,
        contentPadding = PaddingValues(horizontal = 2.dp),
        key = { index -> videos[index].id }
    ) { page ->
        val videoData = videos[page]
        val playerState = rememberPlayerState(uri = videoData.videoUri)
        val interactionSource = remember { MutableInteractionSource() }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                val offset: Float = calculateCurrentOffsetForPage(page)

                transformOrigin = TransformOrigin.Center.copy(0.5f + (offset.sign * 0.5f))
                rotationY = -offset.sign * offset.absoluteValue.coerceIn(0f, 1f) * 30
            }.clickable(interactionSource = interactionSource, null) {}
        ) {
            PageScreen(page, playerState)
        }

        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { source, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        playerState.release()
                    }
                    Lifecycle.Event.ON_PAUSE,
                    Lifecycle.Event.ON_STOP -> {
                        if (currentPage == page) {
                            playerState.pause()
                        }
                    }
                    Lifecycle.Event.ON_RESUME,
                    Lifecycle.Event.ON_START -> {
                        if (currentPage == page) {
                            playerState.play()
                        }
                    }
                    else -> {}
                }
            }
            lifecycle.addObserver(observer)

            onDispose {
                playerState.stop()
                lifecycle.removeObserver(observer)
            }
        }


        val isPressed by interactionSource.collectIsPressedAsState()
        LaunchedEffect(pagerState) {
            snapshotFlow { isPressed }
                .collect { isPressed ->
                    if (isPressed) {
                        playerState.pause()
                    } else if (!pagerState.isScrollInProgress) {
                        playerState.play()
                    }
                }
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.isScrollInProgress }
                .collect { isScrollProgress ->
                    val selectedPage = pagerState.currentPage
                    val pageDiff = (selectedPage - page).absoluteValue
                    val offset = calculateCurrentOffsetForPage(page)

                    if (offset != 0f) {
                        playerState.pause()
                    }

                    if (isScrollProgress) {
                        if (pageDiff <= 1) {
                            playerState.prepare()
                        } else {
                            playerState.stop()
                        }
                    } else {
                        if (pageDiff == 0) {
                            playerState.prepare()
                            playerState.play()
                        } else if (pageDiff == 1) {
                            playerState.stop()
                        }
                    }
                }
        }

        LaunchedEffect(playerState) {
            playerState.isPlayingEnd
                .filter { it }
                .collect {
                    if (page != videos.lastIndex) {
                        pagerState.animateScrollToPage(page + 1)
                        playerState.stop()
                    }
                }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerScope.PageScreen(page: Int, playerState: PlayerState) {

    Box(modifier = Modifier.fillMaxSize()) {
        VideoPlayerScreen(
            playerState.player
        )

//        AnimatedVisibility(
//            visible = !isPlayingState,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.background(
//                Color.Black.copy(0.5f)
//            )
//        ) {
//            AsyncImage(
//                model = ImageRequest.Builder(context)
//                    .data(R.drawable.ic_launcher_foreground)
//                    .crossfade(true)
////                    .fallback(R.drawable.ic_launcher_foreground)
//                    .build(),
//                contentDescription = "",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillBounds
//            )
//        }

        val offset = calculateCurrentOffsetForPage(page)
        val alpha: Float by animateFloatAsState(
            lerp(
                0f,
                1f,
                1f - offset.absoluteValue.coerceIn(0f, 1f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 1f - alpha))
        )

        VideoInfoScreen(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(Color.Transparent)
                .alpha(alpha = alpha),
            playerState,
        )
    }

}

@Composable
fun VideoInfoScreen(modifier: Modifier, playerState: PlayerState) {
    val duration = playerState.duration
    val currP by playerState.currentPosition.collectAsState()

    Column(
        modifier = modifier
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(1.dp)),
            progress = if (duration == 0L) 0f else currP.toFloat() / duration,
            color = Color.White,
            backgroundColor = Color.White.copy(0.5f)
        )
    }
}

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayerScreen(player: Player) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                this.player = player
            }
        }, modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun rememberPlayerState(uri: Uri): PlayerState {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))

                setMediaSource(source)

                playWhenReady = true
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            }
    }

    val coroutine = rememberCoroutineScope()

    return remember {
        PlayerState(exoPlayer, coroutine)
    }
}


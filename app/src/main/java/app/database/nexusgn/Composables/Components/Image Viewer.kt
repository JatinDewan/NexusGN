package app.database.nexusgn.Composables.Components

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.database.nexusgn.Data.Api.Images
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewer(
    showScreenshots: Boolean,
    showImageDots: Boolean = true,
    allImages: List<Images>,
    configuration: Configuration,
    closeFullScreen: () -> Unit,
    imageLocation: Int
) {

    val pagerState = rememberPagerState { allImages.size }
    var showBar by rememberSaveable { mutableStateOf(true) }
    val listGreaterThanOne by remember { derivedStateOf { allImages.size > 1 } }
    val isPortrait by remember {
        derivedStateOf { configuration.orientation == Configuration.ORIENTATION_PORTRAIT }
    }
    var zoom by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val animateSize by animateFloatAsState(targetValue = zoom, label = "")
    val animateOffset by animateOffsetAsState(targetValue = offset, label = "")
    val isZoomInProgress by remember { derivedStateOf { zoom == 1f } }

    LaunchedEffect(imageLocation) {
        delay(200)
        pagerState.scrollToPage(imageLocation)
    }

    AnimatedVisibility(
        visible = showScreenshots,
        enter = fadeIn(tween(200, 300)),
        exit = fadeOut(tween(200, 300))
    ) {
        fullScreen(
            showNavigationBar = true,
            show = showScreenshots
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            offset = Offset.Zero
                            zoom = if (animateSize != 1f) 1f else 1.5f
                            showBar = true
                        },
                        onTap = {
                            showBar = !showBar
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, gesturePan, gestureZoom, _ ->
                            val newOffset = offset + gesturePan

                            zoom = (zoom * gestureZoom).coerceIn(1f, 4f)

                            val maxX = (size.width * (zoom - 1) / 2f)
                            val maxY = (size.height * (zoom - 1) / 2f)

                            offset = if (!isPortrait) {
                                Offset(
                                    newOffset.x.coerceIn(-maxX, maxX),
                                    newOffset.y.coerceIn(-maxY, maxY)
                                )
                            } else {
                                Offset(
                                    newOffset.x.coerceIn(-maxX, maxX),
                                    newOffset.y.coerceIn(0f, 0f)
                                )
                            }
                        }
                    )
                }
                .onSizeChanged { image -> size = image }
                .graphicsLayer {
                    translationX = animateOffset.x
                    translationY = animateOffset.y
                    scaleX = animateSize
                    scaleY = animateSize
                }
                .background(MaterialTheme.colorScheme.background.copy(0.9f)),
            state = pagerState,
            pageSpacing = 20.dp,
            userScrollEnabled = isZoomInProgress,
            contentPadding = PaddingValues(0.dp),
            beyondBoundsPageCount = 0,
            pageSize = PageSize.Fill,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                lowVelocityAnimationSpec = tween(
                    easing = LinearOutSlowInEasing,
                    durationMillis = 1000
                ),
                snapAnimationSpec = spring(stiffness = Spring.StiffnessHigh)
            ),
            key = null,
            pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                Orientation.Horizontal
            ),
            pageContent =  { currentPage ->
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(allImages[currentPage].image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "gameImages",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    filterQuality = FilterQuality.High
                )
            }
        )

        AnimatedVisibility(
            visible = listGreaterThanOne && showImageDots && showBar && isZoomInProgress,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            val maxHeight by remember { derivedStateOf { 15.dp } }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(
                    modifier = Modifier.fillMaxHeight(if (isPortrait) 0.45f else 0.9f)
                )
                Card(
                    modifier = Modifier.height(maxHeight + 10.dp),
                    colors = CardDefaults.cardColors(Color.Transparent),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(allImages.count()) {
                                val derivedSelection by remember {
                                    derivedStateOf { it == pagerState.currentPage }
                                }

                                val colour by animateColorAsState(
                                    targetValue = if (derivedSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary,
                                    animationSpec = tween(300), label = ""
                                )

                                val dotSize by animateDpAsState(
                                    targetValue = if (derivedSelection) maxHeight else 10.dp,
                                    animationSpec = tween(300), label = ""
                                )

                                Card(
                                    modifier = Modifier.size(dotSize),
                                    colors = CardDefaults.cardColors(colour)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) { Text(text = "") }
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { closeFullScreen() }
                            )
                        }
                        .padding(15.dp)
                        .size(40.dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun fullScreen(
    show: Boolean,
    showNavigationBar: Boolean = false,
    isSlideShow: Boolean = false
): WindowInsetsControllerCompat {
    val view = LocalView.current
    val window = (view.context as Activity).window

    return if(show) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if(showNavigationBar) hide(WindowInsetsCompat.Type.navigationBars())
            hide(WindowInsetsCompat.Type.displayCutout())
            window.navigationBarColor =  MaterialTheme.colorScheme.background.toArgb()
            window.statusBarColor = Color.Transparent.toArgb()
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    } else {
        WindowCompat.setDecorFitsSystemWindows(window, isSlideShow)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if(isSlideShow) show(WindowInsetsCompat.Type.statusBars())
            show(WindowInsetsCompat.Type.navigationBars())
            window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
            if(isSlideShow) window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}
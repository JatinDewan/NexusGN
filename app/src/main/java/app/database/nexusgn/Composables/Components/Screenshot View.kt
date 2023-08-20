package app.database.nexusgn.Composables.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.database.nexusgn.Data.ApiDataModel.Images
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun ScreenshotViewer(
    images: List<Images>,
    viewModel: NexusGNViewModel,
    visibility: Boolean
){
    val lazyRowState = rememberLazyListState()
    AnimatedVisibility(
        visible = visibility,
        enter = slideInHorizontally()
    ){
        Column {

            Headers(
                text = viewModel.stringProvider(R.string.Screenshots),
                padding = 10.dp
            )

            LazyRow(
                state = lazyRowState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(9.dp)
            ) {
                items(
                    items = images,
                    key = { screenshotKey -> screenshotKey.id!! }
                ) { screenshots ->
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .clickable {
                                    viewModel.showSelectedImage(images.indexOf(screenshots))
                                    viewModel.showScreenshot(true)
                                }
                                .height(100.dp)
                                .width(130.dp),
                            model = ImageRequest.Builder(context = LocalContext.current)
                                .data(screenshots.image)
                                .crossfade(true)
                                .build(),
                            loading = {
                                LoadingImages()
                            },
                            error = {
                                LoadingImages()
                            },
                            contentDescription = viewModel.stringProvider(R.string.gameImages),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            filterQuality = FilterQuality.None
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingImages() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(0.2f),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.primary
        )
    }
}
package app.database.nexusgn.Composables.Components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
        /**Refactored for use in [DisplayGames]*/

fun GridCards(
    gameDetails: GameInformation,
    scrollToCard:() -> Unit,
    viewModel: NexusGNViewModel,
    modifier: Modifier = Modifier
) {

    val config = LocalConfiguration.current.screenWidthDp.dp
    var isSelected by remember { mutableStateOf(false) }
    val isSelectedDerived = remember { derivedStateOf { isSelected } }

    val animateName = animateIntAsState(
        targetValue = if(isSelectedDerived.value) 4 else 1,
        label = ""
    )
    val animateTags = animateIntAsState(
        targetValue = if(isSelectedDerived.value) 14 else 10,
        label = ""
    )

    Card(
        modifier = Modifier
            .width((config / 2) -20.dp)
            .height(240.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        BoxWithConstraints {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .clickable { scrollToCard() },
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(gameDetails.backgroundImage)
                        .crossfade(true)
                        .build(),
                    loading = { LoadingImages() },
                    error = { LoadingImages() },
                    contentDescription = stringResource(id = R.string.gameImages),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    filterQuality = FilterQuality.None
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { isSelected = !isSelected })
                        }
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background.copy(0.9f)),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {

                        PlatformLogos(
                            viewModel = viewModel,
                            gameInformation = gameDetails,
                            size = 8.dp
                        )

                        gameDetails.name?.let { name ->
                            Text(
                                text = name,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = animateName.value,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )
                        }

                        Tags(
                            genre = gameDetails,
                            textSize = animateTags.value,
                            enabled = isSelectedDerived.value,
                            showAll = isSelectedDerived.value,
                            viewModel = viewModel,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
        /**Refactored for use in [SearchBar]*/
fun SearchCards(
    viewModel: NexusGNViewModel,
    gameDetails: GameInformation,
    scrollToCard:() -> Unit
) {
    Row(
        modifier = Modifier
            .heightIn(min = 90.dp)
            .fillMaxWidth()
            .clickable { scrollToCard() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            if(!gameDetails.backgroundImage.isNullOrEmpty()){
                Card(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(Color.Transparent)
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxHeight(),
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(gameDetails.backgroundImage)
                            .crossfade(true)
                            .build(),
                        loading = { LoadingImages() },
                        contentDescription = stringResource(id = R.string.gameImages),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        filterQuality = FilterQuality.None
                    )
                }
            }

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                PlatformLogos(
                    viewModel = viewModel,
                    gameInformation = gameDetails,
                    size = 10.dp
                )

                gameDetails.name?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                TagsSearch(
                    genre = gameDetails, textSize = 14,
                )
            }
        }
    }
}

@Composable
        /**Refactored for use in [DisplayGames]*/
fun HighlightedGameForCategory(
    viewModel: NexusGNViewModel,
    gameInformation: GameInformation,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .graphicsLayer { alpha = 1f }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = colors,
                                startY = 250.0f,
                                endY = 750.0f
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .clickable { onClick() },
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(gameInformation.backgroundImage)
                    .crossfade(true)
                    .build(),
                loading = {
                    LoadingImages()
                },
                error = {
                    LoadingImages()
                },
                contentDescription = stringResource(id = R.string.gameImages),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                filterQuality = FilterQuality.None
            )

            gameInformation.name?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Tags(
                        viewModel = viewModel,
                        genre = gameInformation,
                        textSize = 10,
                        enabled = false,
                        onClick = ::TODO,
                        showAll = true
                    )
                }
            }
        }
    }
}
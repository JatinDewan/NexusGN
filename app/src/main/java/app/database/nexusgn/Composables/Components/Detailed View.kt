package app.database.nexusgn.Composables.Components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Data.ApiDataModel.GameDetails
import app.database.nexusgn.Data.ApiDataModel.GameInformation
import app.database.nexusgn.Data.ApiDataModel.Screenshots
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
        /**Refactored for use as overlay in [DisplayGames]*/
fun GameCardsDetailed(
    viewModel: NexusGNViewModel,
    gameInformation: GameInformation,
    gameDetails: GameDetails,
    screenshots: Screenshots,
    visibility: Boolean
) {

    val lazyState = rememberLazyListState()
    var delayedVisibility by rememberSaveable { mutableStateOf(false) }
    val delayedVisibilityDerived by remember { derivedStateOf { delayedVisibility } }
    val indexView = remember { derivedStateOf { lazyState.firstVisibleItemIndex } }
    val sheetState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(visibility) {
        delayedVisibility = if (visibility) { delay(400); true } else false
    }

    LaunchedEffect(indexView.value) {
        viewModel.hideSearch(indexView.value > 0)
    }

    fullScreen(
        show = visibility,
        isSlideShow = true
    )

    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(tween(400)) + fadeIn(tween(400)),
        exit = fadeOut(tween(100))
    ){
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null
            ) {
                BottomSheetScaffold(
                    scaffoldState = sheetState,
                    sheetContent = {
                        LazyColumn(
                            state = lazyState,
                            modifier = Modifier
                                .heightIn(max = (LocalConfiguration.current.screenHeightDp.dp - 300.dp))
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {

                                    screenshots.results.let {
                                        ScreenshotViewer(
                                            images = it,
                                            viewModel = viewModel,
                                            visibility = delayedVisibilityDerived
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        AboutGameDetails(
                                            targetAnimation = delayedVisibilityDerived,
                                            gameDetails = gameDetails,
                                            viewModel = viewModel
                                        )

                                        AdditionalDetailsColumns(
                                            targetAnimation = delayedVisibilityDerived,
                                            gameInformation = gameInformation,
                                            gameDetails = gameDetails,
                                            viewModel = viewModel,
                                            context = context
                                        )

                                        RAWGWaterMark(
                                            isVisible = delayedVisibilityDerived
                                        )
                                    }
                                }
                            }
                        }
                    },
                    sheetPeekHeight = LocalConfiguration.current.screenHeightDp.dp / 2,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    sheetContainerColor = MaterialTheme.colorScheme.secondary,
                    sheetShadowElevation = 10.dp,
                    sheetShape = RoundedCornerShape(0.dp),
                    sheetTonalElevation = 10.dp,
                    sheetDragHandle = {
                        DetailedPageHeader(
                            animatedVisibility = delayedVisibilityDerived,
                            gameInformation = gameInformation,
                            viewModel = viewModel,
                            gameDetails = gameDetails,
                        )
                    }
                ) {

                    BackgroundImageDetailedView(
                        gameBackgroundImage = gameInformation.backgroundImage,
                        ifScrolledUp = sheetState.bottomSheetState.targetValue == SheetValue.Expanded,
                    )
                }
            }
        }
    }

}
@Composable
        /**Refactored for [GameCardsDetailed]*/
fun AdditionalDetailsColumns(
    targetAnimation: Boolean,
    gameInformation: GameInformation,
    gameDetails: GameDetails,
    viewModel: NexusGNViewModel,
    context: Context
) {
    if (targetAnimation) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gameInformation.platforms?.let {
                    PlatformsTabs(
                        list = it,
                        viewModel = viewModel
                    )
                }
                gameInformation.genres?.let { genre ->
                    GenreTabs(
                        list = genre,
                        viewModel = viewModel
                    )
                }
                AllLinks(
                    targetAnimation = targetAnimation,
                    gameDetails = gameDetails,
                    viewModel = viewModel,
                    context = context
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gameInformation.released?.let { dateReleased ->
                    viewModel.date.convertDateFormat(dateReleased)
                }?.let { ReleaseDates(
                    date = it,
                    viewModel = viewModel
                ) }

                gameDetails.esrbRating?.name?.let {
                    Rating(
                        rating = it,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**Refactored for [GameCardsDetailed]*/
fun AboutGameDetails(
    targetAnimation: Boolean,
    gameDetails: GameDetails,
    viewModel: NexusGNViewModel
){
    if (targetAnimation) {
        var maxLines by rememberSaveable { mutableStateOf(false) }
        val maxLinesDerived = remember { derivedStateOf { maxLines } }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Headers(text = viewModel.stringProvider(R.string.Description))

                gameDetails.rawDescription?.let {
                    LimitedTextView(
                        text = it,
                        maxWords = if(maxLinesDerived.value)  10000 else 50,
                        viewModel = viewModel
                    )
                }
            }

            val checkStringDerived = remember {
                derivedStateOf{ viewModel.checkStringLength(gameDetails.rawDescription) }
            }
            if (checkStringDerived.value) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false,
                ) {
                    Card(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = {
                            maxLines = !maxLines
                        },
                        colors = CardDefaults.cardColors(
                            Color.Transparent
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = if (maxLinesDerived.value) viewModel.stringProvider(R.string.Show_less) else viewModel.stringProvider(R.string.Show_more),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
        /**Refactored for [GameCardsDetailed]*/
fun DetailedPageHeader(
    animatedVisibility: Boolean,
    gameInformation: GameInformation,
    viewModel: NexusGNViewModel,
    gameDetails: GameDetails
) {

    AnimatedVisibility(
        visible = animatedVisibility,
        enter = fadeIn(tween(300, 200))
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(0.dp),
        ){
            Box(
                modifier = Modifier.fillMaxWidth()
            ){
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .fillMaxWidth(0.7f),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                shape = RoundedCornerShape(5.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onSecondary)
                            ) {
                                gameInformation.released?.let { gameRelease ->
                                    Text(
                                        modifier = Modifier.padding(3.dp),
                                        text = viewModel.date.convertDateFormat(gameRelease),
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            PlatformLogos(
                                viewModel = viewModel,
                                gameInformation = gameInformation,
                                size = 15.dp
                            )
                        }

                        Text(
                            text = viewModel.stringProvider(R.string.Average_Playtime,gameDetails.playtime.toString()).uppercase()/*"Average Playtime: ${gameDetails.playtime} hours".uppercase()*/,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 12.sp,
                        )
                        gameInformation.name?.let { gameName ->
                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                text = gameName,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    gameDetails.metacritic?.let {
                        Metacritic(
                            score = it,
                            colour = viewModel.gameRatingColourIndication(it),
                        )
                    }
                }
            }
        }
    }
}


@Composable
        /**Refactored for [GameCardsDetailed]*/
fun BackgroundImageDetailedView(
    ifScrolledUp: Boolean,
    gameBackgroundImage: String?,
){

    val animateImage by animateFloatAsState(
        targetValue = if (ifScrolledUp) 0.35f else 0.55f,
        tween(200),
        label = ""
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(animateImage),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        shape = RoundedCornerShape(0.dp),
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(gameBackgroundImage)
                .crossfade(true)
                .build(),
            loading = { LoadingImages() },
            error = { LoadingImages() },
            contentDescription = "gameImages",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            filterQuality = FilterQuality.High
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
        /**Refactored for use in [GameDetailsHighlighted], [DetailedPageHeader], [GridCards] & [SearchCards]*/
fun PlatformLogos(
    viewModel: NexusGNViewModel,
    gameInformation: GameInformation,
    size: Dp
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        gameInformation.platforms?.let { platforms -> viewModel.icons.distinctPlatforms(platforms) }?.forEach { Icons ->

            val isConsolePlaystation = remember {
                derivedStateOf { Icons.first == viewModel.stringProvider(R.string.playstation) }
            }

            Icon(
                modifier = Modifier
                    .size(if (isConsolePlaystation.value) size + 3.dp else size)
                    .fillMaxHeight(),
                imageVector = ImageVector.vectorResource(id = Icons.second),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
        /**Refactored for use in [GameCardsDetailed] & [SearchBar]*/
fun RAWGWaterMark(
    isVisible: Boolean
) {
    if(isVisible){
        Text(
            text = stringResource(id = R.string.poweredBy),
            color = MaterialTheme.colorScheme.onTertiary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 8.sp
        )
    }
}
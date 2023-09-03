package app.database.nexusgn.Composables.Components

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
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
import androidx.compose.runtime.collectAsState
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
import app.database.nexusgn.Data.Api.GameDetails
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.InteractionElements
import app.database.nexusgn.Data.UiState.ScreenshotsApiResponse
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**Refactored for use as overlay in [DisplayGames]*/
fun GameCardsDetailed(
    viewModel: NexusGNViewModel,
    gameDetails: GameDetailsApiResponse,
    screenshots: ScreenshotsApiResponse,
) {
    val interactionUiState by viewModel.uiStateInteractionSource.collectAsState()
    val lazyState = rememberLazyListState()
    val indexView = remember { derivedStateOf { lazyState.firstVisibleItemIndex } }
    val sheetState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val expandView by animateFloatAsState(
        animationSpec = tween(200, easing = LinearOutSlowInEasing),
        targetValue = when (gameDetails) {
            is GameDetailsApiResponse.Loading -> 0.3f
            is GameDetailsApiResponse.Success -> 1f
            else -> 0f
        },
        label = ""
    )

    LaunchedEffect(indexView.value) { viewModel.hideSearch(indexView.value > 0) }

    Box(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ){
        fullScreen(
            show = gameDetails is GameDetailsApiResponse.Success,
            isSlideShow = true
        )

        LoadingDetailedView(
            expandView = expandView,
            gameDetails = gameDetails
        )

        AnimatedContent(
            targetState = gameDetails,
            transitionSpec = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 500, easing = EaseIn)
                ) + fadeIn(tween(durationMillis = 200, easing = EaseIn)) togetherWith
                    fadeOut(tween(durationMillis = 0))
            },
            label = ""
        ) { details ->

            if(details is GameDetailsApiResponse.Success){
               MainDetailedView(
                   sheetState = sheetState,
                   lazyState = lazyState,
                   viewModel = viewModel,
                   context = context,
                   screenshots = screenshots,
                   details = details.gameDetails,
                   interactionUiState = interactionUiState
               )
            }

            if (details is GameDetailsApiResponse.Error){
                NoGamesFound(viewModel = viewModel)
            }

        }
    }
    
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDetailedView(
    sheetState: BottomSheetScaffoldState,
    lazyState: LazyListState,
    viewModel: NexusGNViewModel,
    context: Context,
    screenshots: ScreenshotsApiResponse,
    details: GameDetails,
    interactionUiState: InteractionElements
){
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            GameDetailsSheet(
                lazyState = lazyState,
                viewModel = viewModel,
                context = context,
                screenshots = screenshots,
                gameDetails = details,
                interactionUiState = interactionUiState,
            )
        },
        sheetPeekHeight = LocalConfiguration.current.screenHeightDp.dp / 2,
        containerColor = MaterialTheme.colorScheme.secondary,
        sheetContainerColor = MaterialTheme.colorScheme.secondary,
        sheetShadowElevation = 10.dp,
        sheetShape = RoundedCornerShape(0.dp),
        sheetTonalElevation = 10.dp,
        sheetDragHandle = {
            interactionUiState.gameHolder?.let { game ->
                DetailedPageHeader(
                    gameInformation = game,
                    viewModel = viewModel,
                    gameDetails = details,
                )
            }
        }
    ) {
        BackgroundImageDetailedView(
            gameBackgroundImage = interactionUiState.gameHolder?.backgroundImage,
            isScrolledUp = sheetState.bottomSheetState.targetValue == SheetValue.Expanded,
        )
    }
}

@Composable
fun GameDetailsSheet(
    lazyState: LazyListState,
    viewModel: NexusGNViewModel,
    context: Context,
    gameDetails: GameDetails,
    interactionUiState: InteractionElements,
    screenshots: ScreenshotsApiResponse
){

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

                if(screenshots is ScreenshotsApiResponse.Success) {
                    ScreenshotViewer(
                        images = screenshots.screenshots.results,
                        viewModel = viewModel,
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
                        gameDetails = gameDetails,
                        viewModel = viewModel
                    )
                    interactionUiState.gameHolder?.let { game ->
                        AdditionalDetailsColumns(
                            gameInformation = game,
                            gameDetails = gameDetails,
                            viewModel = viewModel,
                            context = context
                        )
                    }
                    RAWGWaterMark()
                }
            }
        }
    }
}

@Composable
fun LoadingDetailedView(
    expandView: Float,
    gameDetails: GameDetailsApiResponse
){
    Card(
        modifier = Modifier.fillMaxHeight(expandView),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        shape = RoundedCornerShape(30.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if(gameDetails is GameDetailsApiResponse.Loading){
                Column {
                    Spacer(modifier = Modifier.height(55.dp))
                    LoadingPage(PaddingValues(0.dp))
                }
            }
        }
    }
}

@Composable
        /**Refactored for [GameCardsDetailed]*/
fun AdditionalDetailsColumns(
    gameInformation: GameInformation,
    gameDetails: GameDetails,
    viewModel: NexusGNViewModel,
    context: Context
) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**Refactored for [GameCardsDetailed]*/
fun AboutGameDetails(
    gameDetails: GameDetails,
    viewModel: NexusGNViewModel
){
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

@Composable
        /**Refactored for [GameCardsDetailed]*/
fun DetailedPageHeader(
    gameInformation: GameInformation,
    viewModel: NexusGNViewModel,
    gameDetails: GameDetails
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(0.dp),
    ){
        Box(modifier = Modifier.fillMaxWidth()){
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
                        text = viewModel.stringProvider(
                            name = R.string.Average_Playtime,
                            extra = gameDetails.playtime.toString()
                        ).uppercase(),
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
                gameDetails.metacritic?.let { score ->
                    Metacritic(
                        score = score,
                        colour = viewModel.gameRatingColourIndication(score),
                    )
                }
            }
        }
    }
}

@Composable
        /**Refactored for [GameCardsDetailed]*/
fun BackgroundImageDetailedView(
    isScrolledUp: Boolean,
    gameBackgroundImage: String?,
){

    val animateHeight by animateFloatAsState(
        targetValue = if (isScrolledUp) 0.35f else 0.65f,
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(animateHeight),
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
) {
    Text(
        text = stringResource(id = R.string.poweredBy),
        color = MaterialTheme.colorScheme.onTertiary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontSize = 8.sp
    )
}
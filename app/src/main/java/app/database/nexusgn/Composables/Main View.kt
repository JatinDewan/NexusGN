package app.database.nexusgn.Composables

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import app.database.nexusgn.Composables.Components.ImageViewer
import app.database.nexusgn.Data.UiState.AllGamesApiResponse
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.ScreenshotsApiResponse
import app.database.nexusgn.ViewModel.NexusGNViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun MainView(
    configuration: Configuration,
    viewModel: NexusGNViewModel,
    allGamesApi: AllGamesApiResponse,
    screenShots: ScreenshotsApiResponse,
    gameDetails: GameDetailsApiResponse
){
    val interactionUiState by viewModel.uiStateInteractionSource.collectAsState()
    val listUiState by viewModel.uiStateList.collectAsState()

    val lazyListState = rememberLazyListState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val searchListState = rememberLazyListState()
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val textUsed by remember { derivedStateOf { viewModel.searchPhrase.text.isNotEmpty() } }
    val canBarScroll by remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 1  && !interactionUiState.clickedCard } }

    var barOffsetY by remember { mutableStateOf(0f) }
    val animateOffset by animateFloatAsState(
        targetValue = barOffsetY,
        animationSpec = tween(50),
        label = ""
    )

    LaunchedEffect(interactionUiState){ if(interactionUiState.isSearchFocused == true) barOffsetY = 0f }
    LaunchedEffect(drawerState.currentValue) { if(drawerState.isOpen) focusManager.clearFocus() }
    BackHandler(onBack = { viewModel.onBackHandler(focusManager) })

    LaunchedEffect(drawerState.targetValue){
        drawerState.animateTo(
            drawerState.targetValue,
            tween(400,50)
        )
    }

    val nestedScrollConnection by remember {
        derivedStateOf{
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (canBarScroll) {
                        coroutineScope.launch {
                            withContext(Dispatchers.Default) {
                                val deltaY = available.y / 250f
                                val clampedOffset = (barOffsetY / 250f - deltaY).coerceIn(0f, 1f)
                                barOffsetY = clampedOffset * 250f
                            }
                        }
                    }
                    return Offset.Zero
                }
                override suspend fun onPreFling(available: Velocity): Velocity {
                    if(canBarScroll){
                        barOffsetY = if (barOffsetY >= 125f) 250f else 0f
                    }
                    return super.onPreFling(available)
                }
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.secondary
    ){
        ModalNavigationDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerState = drawerState,
            drawerContent = {
                Surface(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    NavigationMain(
                        viewModel = viewModel,
                        closeNavigation = { coroutineScope.launch { drawerState.close() } },
                        listState = lazyListState
                    )
                }
            },
            scrimColor = MaterialTheme.colorScheme.background.copy(0.7f),
            gesturesEnabled = !interactionUiState.clickedCard
        ) {
            DisplayGames(
                viewModel = viewModel,
                lazyListState = lazyListState,
                focusManager = focusManager,
                nestedScrollConnection = nestedScrollConnection,
                showSearch = { barOffsetY = 0f },
                allGames = listUiState.allGamesUi ?: emptyList(),
                allGamesApi = allGamesApi,
                screenshots = screenShots,
                gameDetails = gameDetails
            )
            // for search, stick in a vertical pager with nested scroll for having it auto pop in and out when not scrolling
            EmbeddedSearchBar(
                viewModel = viewModel,
                isCardOpen = interactionUiState.clickedCard,
                focusManager = focusManager,
                focusRequester = focusRequester,
                startSearch = {
                    viewModel.startSearch(focusManager, searchListState)
                },
                endSearch = { viewModel.endSearch(focusManager) },
                openDrawer = { coroutineScope.launch { drawerState.open() } },
                offset = IntOffset(x = 0, y = -animateOffset.roundToInt()),
                isSearchFocused = interactionUiState.isSearchFocused,
                isSearchActive = textUsed,
                suggestedSearchList = listUiState.suggestedSearchResults ?: emptyList(),
                relatedSearchList = listUiState.relatedSearchResults ?: emptyList(),
                suggestedShow = !listUiState.suggestedSearchResults.isNullOrEmpty(),
                relatedShow = !listUiState.relatedSearchResults.isNullOrEmpty(),
                clearSearch = { viewModel.clearSearch(focusRequester) },
                unsuccessfulSearch = interactionUiState.unsuccessfulSearch,
                searchListState = searchListState,
            )

            if(screenShots is ScreenshotsApiResponse.Success){
                interactionUiState.currentImage?.let { imageIndex ->
                    ImageViewer(
                        showScreenshots = interactionUiState.showScreenshot,
                        allImages = screenShots.screenshots.results,
                        imageLocation = imageIndex,
                        closeFullScreen = { viewModel.showScreenshot(false) },
                        configuration = configuration
                    )
                }
            }
        }
    }
}
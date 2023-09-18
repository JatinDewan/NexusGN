package app.database.nexusgn.Composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Composables.Components.GameCardsDetailed
import app.database.nexusgn.Composables.Components.GridCards
import app.database.nexusgn.Composables.Components.HighlightedGameForCategory
import app.database.nexusgn.Composables.Components.LoadingPage
import app.database.nexusgn.Composables.Components.NoGamesFound
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.UiState.AllGamesApiResponse
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.GamesUiState
import app.database.nexusgn.Data.UiState.ScreenshotsApiResponse
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
        /**Refactored for use in [MainView]*/
fun DisplayGames(
    viewModel: NexusGNViewModel,
    nestedScrollConnection: NestedScrollConnection,
    lazyListState: LazyListState,
    allGames: List<GameInformation>,
    focusManager: FocusManager,
    showSearch:() -> Unit,
    gameDetails: GameDetailsApiResponse,
    screenshots: ScreenshotsApiResponse,
    allGamesApi: AllGamesApiResponse,
){
    val gameUiState by viewModel.uiStateGameDetails.collectAsState()
    val snackState = remember { SnackbarHostState() }

    val lastVisibleItemIndex by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ==
            lazyListState.layoutInfo.totalItemsCount - 1
        }
    }

    if(allGamesApi is AllGamesApiResponse.Error && allGames.isNotEmpty()) {
        LaunchedEffect(null) {
            snackState.showSnackbar("")
        }
    }

    if(viewModel.apiHandler.allGames.size > 39 && lastVisibleItemIndex){
        if(allGamesApi !is AllGamesApiResponse.Error){
            LaunchedEffect(null) {
                delay(700)
                viewModel.nextPage()
            }
        }
    }

    CompositionLocalProvider(
       LocalOverscrollConfiguration provides null,
    ){
        AllGamesView(
            allGames = allGames,
            allGamesApi = allGamesApi,
            lazyListState = lazyListState,
            gameUiState = gameUiState,
            viewModel = viewModel,
            focusManager = focusManager,
            nestedScrollConnection = nestedScrollConnection,
            padding = PaddingValues(0.dp),
            showSearch = showSearch::invoke,
        )

        when {
            allGamesApi is AllGamesApiResponse.Loading -> LoadingPage()
            allGamesApi is AllGamesApiResponse.Error && allGames.isEmpty() -> NoGamesFound(viewModel)
        }

        SnackBarMessage(
            viewModel = viewModel,
            snackState = snackState
        )

        GameCardsDetailed(
            viewModel = viewModel,
            gameDetails = gameDetails,
            screenshots = screenshots,
        )
    }
}

@Composable
fun AllGamesView(
    allGames: List<GameInformation>,
    allGamesApi: AllGamesApiResponse,
    lazyListState: LazyListState,
    gameUiState: GamesUiState,
    viewModel: NexusGNViewModel,
    focusManager: FocusManager,
    nestedScrollConnection: NestedScrollConnection,
    padding: PaddingValues,
    showSearch: () -> Unit,
){
    AnimatedVisibility(
        visible = allGamesApi is AllGamesApiResponse.Success ||
                allGamesApi is AllGamesApiResponse.Error && allGames.isNotEmpty(),
        enter = slideInVertically { 500 },
        exit = slideOutVertically { 500 }
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(nestedScrollConnection)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            item { }
            item { Spacer(modifier = Modifier.height(65.dp)) }
            item {
                if (allGames.isNotEmpty()) {
                    ListedGameHeaders(
                        text = stringResource(id = R.string.Highlighted)
                    )
                }
            }
            item {
                gameUiState.highlightedGame?.let { highlightedGame ->
                    gameUiState.pageHeader?.let {
                        HighlightedGameForCategory(
                            gameInformation = highlightedGame,
                            onClick = {
                                viewModel.navigateToDetailedView(
                                    focusManager = focusManager,
                                    entries = highlightedGame
                                )
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
            item { }
            item {
                if (allGames.isNotEmpty()) {
                    gameUiState.pageHeader?.let { header ->
                        ListedGameHeaders(text = header)
                    }
                }
            }
            items(items = allGames.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { entries ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            GridCards(
                                gameDetails = entries,
                                scrollToCard = {
                                    showSearch()
                                    viewModel.navigateToDetailedView(
                                        focusManager = focusManager,
                                        entries = entries
                                    )
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListedGameHeaders(
    text: String,
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = text,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SnackBarMessage(
    viewModel: NexusGNViewModel,
    snackState: SnackbarHostState
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(snackState) {
            Box(contentAlignment = Alignment.BottomCenter) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.elevatedCardElevation(10.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp )){
                        Text(
                            text = viewModel.stringProvider(R.string.NoMoreResults),
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
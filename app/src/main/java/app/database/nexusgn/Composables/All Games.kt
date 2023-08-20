package app.database.nexusgn.Composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import app.database.nexusgn.Data.ApiDataModel.GameInformation
import app.database.nexusgn.Data.UiState.AllGamesApiResponse
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.ScreenshotsApiResponse
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel

@OptIn(ExperimentalAnimationApi::class)
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
    allGamesApi: AllGamesApiResponse
){
    val gameUiState by viewModel.uiStateGameDetails.collectAsState()
    val interactionUiState by viewModel.uiStateInteractionSource.collectAsState()

    val lastVisibleItemIndex = remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1
        }
    }

    if(lastVisibleItemIndex.value){
        LaunchedEffect(null) {
            if (lastVisibleItemIndex.value) viewModel.nextPage()
        }
    }

    when(allGamesApi) {
        is AllGamesApiResponse.Success -> {
            AnimatedContent(
                targetState = gameUiState.pageHeader,
                transitionSpec = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        tween(600,400)
                    ) togetherWith fadeOut(tween(0))
                }, label = ""
            ){ state ->
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(10.dp),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .nestedScroll(nestedScrollConnection)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    item{  }

                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }

                    item {
                        AnimatedVisibility(visible = allGames.isNotEmpty()) {
                            ListedGameHeaders(text = stringResource(id = R.string.Highlighted))
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

                    item{ }

                    item{
                        AnimatedVisibility(visible = allGames.isNotEmpty()) {
                            gameUiState.pageHeader?.let { header -> ListedGameHeaders(text = header) }
                        }
                    }

                    items(
                        items = allGames.chunked(2)
                    ) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowItems.forEach { entries ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
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

//                    item {
//                        if (allGamesApi is AllGamesApiResponse.Loading) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Center,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Card(
//                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
//                                ) {
//                                    Column(
//                                        modifier = Modifier.fillMaxWidth(0.5f),
//                                        verticalArrangement = Arrangement.Center,
//                                        horizontalAlignment = Alignment.CenterHorizontally
//                                    ) {
//                                        CircularProgressIndicator(
//                                            modifier = Modifier
//                                                .padding(10.dp)
//                                                .size(50.dp),
//                                            color = MaterialTheme.colorScheme.tertiary,
//                                            trackColor = MaterialTheme.colorScheme.primary
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
        
        is AllGamesApiResponse.Loading -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(50.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        is AllGamesApiResponse.Error -> {
            Column(modifier = Modifier.padding(vertical = 100.dp)){
                Text(text = "${allGamesApi.exception}")
            }
        }
    }

    
    
//    AnimatedContent(
//        targetState = gameUiState.pageHeader,
//        transitionSpec = {
//            slideIntoContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                tween(600,400)
//            ) togetherWith fadeOut(tween(0))
//        }, label = ""
//    ){ state ->
//        LazyColumn(
//            state = lazyListState,
//            contentPadding = PaddingValues(10.dp),
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.background)
//                .nestedScroll(nestedScrollConnection)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(15.dp)
//        ) {
//
//            item{  }
//
//            item {
//                Spacer(modifier = Modifier.height(50.dp))
//            }
//
//            item {
//                AnimatedVisibility(visible = allGames.isNotEmpty()) {
//                    ListedGameHeaders(text = stringResource(id = R.string.Highlighted))
//                }
//            }
//
//            item {
//                gameUiState.highlightedGame?.let { highlightedGame ->
//                    gameUiState.pageHeader?.let {
//                        HighlightedGameForCategory(
//                            gameInformation = highlightedGame,
//                            onClick = {
//                                viewModel.navigateToDetailedView(
//                                    focusManager = focusManager,
//                                    entries = highlightedGame
//                                )
//                            },
//                            viewModel = viewModel
//                        )
//                    }
//                }
//            }
//
//            item{ }
//
//            item{
//                AnimatedVisibility(visible = allGames.isNotEmpty()) {
//                    gameUiState.pageHeader?.let { header -> ListedGameHeaders(text = header) }
//                }
//            }
//
//            items(
//                items = allGames.chunked(2)
//            ) { rowItems ->
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp),
//                ) {
//                    rowItems.forEach { entries ->
//                        Box(
//                            modifier = Modifier.weight(1f),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            GridCards(
//                                gameDetails = entries,
//                                scrollToCard = {
//                                    showSearch()
//                                    viewModel.navigateToDetailedView(
//                                        focusManager = focusManager,
//                                        entries = entries
//                                    )
//                                },
//                                viewModel = viewModel
//                            )
//                        }
//                    }
//                }
//            }
//
//            item {
//                if (allGamesApi is AllGamesApiResponse.Loading) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Card(
//                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
//                        ) {
//                            Column(
//                                modifier = Modifier.fillMaxWidth(0.5f),
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier
//                                        .padding(10.dp)
//                                        .size(50.dp),
//                                    color = MaterialTheme.colorScheme.tertiary,
//                                    trackColor = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    if(gameDetails is GameDetailsApiResponse.Success){
        if(screenshots is ScreenshotsApiResponse.Success){
            interactionUiState.gameHolder?.let { gameContainer ->
                GameCardsDetailed(
                    gameInformation = gameContainer,
                    visibility = interactionUiState.clickedCard,
                    gameDetails = gameDetails.gameDetails,
                    screenshots = screenshots.screenshots,
                    viewModel = viewModel
                )
            }
        }
    }

}

@Composable
fun ListedGameHeaders(
    text: String
){
    Row(
        modifier = Modifier.fillMaxWidth()
    ){
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
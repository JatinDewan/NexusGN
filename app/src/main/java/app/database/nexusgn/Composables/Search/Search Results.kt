package app.database.nexusgn.Composables.Search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Composables.Components.LoadingImages
import app.database.nexusgn.Composables.Components.LoadingPage
import app.database.nexusgn.Composables.Components.NoGamesFound
import app.database.nexusgn.Composables.Components.SearchCards
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.UiState.SearchApiResponse
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun SearchResultsDisplay(
    suggestedSearchList: List<GameInformation>,
    suggestedShow: Boolean,
    relatedSearchList: List<GameInformation>,
    relatedShow: Boolean,
    viewModel: NexusGNViewModel,
    focusManager: FocusManager,
    searchListState: LazyListState,
    searchApiResponse: SearchApiResponse,
) {
    
    if(searchListState.isScrollInProgress) {
        LaunchedEffect(null) {
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Crossfade(
            targetState = searchApiResponse,
            animationSpec = tween(300),
            label = ""
        ) { searchState ->
            when (searchState) {
                is SearchApiResponse.Idle -> SavedSearchResults(
                    viewModel = viewModel,
                    focusManager = focusManager,
                    lazyListState = searchListState
                )

                is SearchApiResponse.Loading -> LoadingPage()

                is SearchApiResponse.Success -> SearchResultLists(
                    viewModel = viewModel,
                    searchListState = searchListState,
                    showSuggested = suggestedShow,
                    showRelated = relatedShow,
                    suggestedSearchList = suggestedSearchList,
                    relatedSearchList = relatedSearchList,
                    focusManager = focusManager
                )

                is SearchApiResponse.Error -> NoGamesFound(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultLists(
    viewModel: NexusGNViewModel,
    searchListState: LazyListState,
    showSuggested: Boolean,
    showRelated: Boolean,
    suggestedSearchList: List<GameInformation>,
    relatedSearchList: List<GameInformation>,
    focusManager: FocusManager,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = searchListState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        stickyHeader {
            if (showSuggested) {
                Column(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = viewModel.stringProvider(R.string.Recommended),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }

        items(
            items = suggestedSearchList,
            key = { gameId -> gameId.id!! }
        ) { entries ->
            SearchCards(
                viewModel = viewModel,
                gameDetails = entries,
                scrollToCard = {
                    viewModel.navigateToDetailedView(
                        focusManager = focusManager,
                        entries = entries,
                    )
                    viewModel.update(TextFieldValue(""))
                }
            )
        }

        stickyHeader {
            if (showRelated) {
                Column(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = viewModel.stringProvider(R.string.Related),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }

        items(
            items = relatedSearchList,
            key = { gameId -> gameId.id!! }
        ) { entries ->
            SearchCards(
                viewModel = viewModel,
                gameDetails = entries,
                scrollToCard = {
                    viewModel.navigateToDetailedView(
                        focusManager = focusManager,
                        entries = entries,
                    )
                    viewModel.update(TextFieldValue(""))
                }
            )
        }
    }
}

@Composable
fun SavedSearchResults(
    viewModel: NexusGNViewModel,
    focusManager: FocusManager,
    lazyListState: LazyListState
){
    val listUiState by viewModel.uiStateList.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(
            items = listUiState.savedSearch?.reversed() ?: emptyList()
        ) { entries ->
            Row(
                modifier = Modifier
                    .clickable {
                        viewModel.update(TextFieldValue(entries.searchPhrase))
                        viewModel.startSearch(focusManager, lazyListState)
                    }
                    .height(35.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Update,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondary
                )

                Text(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    text = entries.searchPhrase,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    modifier = Modifier.size(30.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(Color.Transparent)
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxHeight(),
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(entries.savedSuggestedResults.first().backgroundImage)
                            .crossfade(true)
                            .build(),
                        loading = { LoadingImages() },
                        contentDescription = stringResource(id = R.string.gameImages),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        filterQuality = FilterQuality.None
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
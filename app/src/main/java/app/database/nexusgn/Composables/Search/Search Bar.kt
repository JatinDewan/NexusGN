package app.database.nexusgn.Composables.Search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.UiState.SearchApiResponse
import app.database.nexusgn.Data.Utilities.conditional
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel

@Composable
fun EmbeddedSearchBar(
    viewModel: NexusGNViewModel,
    isCardOpen: Boolean,
    startSearch:() -> Unit,
    endSearch:() -> Unit,
    clearSearch:() -> Unit,
    openDrawer:() -> Unit,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    offset: IntOffset,
    isSearchFocused: Boolean,
    suggestedSearchList: List<GameInformation>,
    relatedSearchList: List<GameInformation>,
    isSearchActive: Boolean,
    suggestedShow: Boolean,
    relatedShow: Boolean,
    searchListState: LazyListState,
    searchApiResponse: SearchApiResponse
) {

    val searchColour by animateColorAsState(
        targetValue = if(isCardOpen && (!isSearchActive && !isSearchFocused))
            Color.Transparent else MaterialTheme.colorScheme.background,
        animationSpec = tween(100),
        label = ""
    )

    val dividerColour by animateColorAsState(
        targetValue = if(isCardOpen && (!isSearchActive && !isSearchFocused))
            Color.Transparent else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(200),
        label = ""
    )

    val searchBackgroundFocus by animateColorAsState(
        targetValue = if(isSearchFocused) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(100),
        label = ""
    )

    val windowInsetsPadding = WindowInsets.statusBars

    Column(
        modifier = Modifier
            .fillMaxSize()
            .conditional(isCardOpen){ windowInsetsPadding(windowInsetsPadding) }
    ) {
        Card(
            modifier = Modifier.offset { offset },
            colors = CardDefaults.cardColors(searchColour),
            shape = RoundedCornerShape(0.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .heightIn(min = 55.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconSelection(
                        isSearchFocused = isSearchFocused,
                        isCardOpen = isCardOpen,
                        isSearchActive = isSearchActive,
                        openDrawer = openDrawer::invoke,
                        endSearch = endSearch::invoke
                    )

                    SearchBarLogo(
                        isSearchFocused = isSearchFocused,
                        isCardOpen = isCardOpen,
                        isSearchActive = isSearchActive
                    )

                    SearchBar(
                        isSearchFocused = isSearchFocused,
                        viewModel = viewModel,
                        searchBackgroundFocus = searchBackgroundFocus,
                        endSearch = clearSearch::invoke,
                        startSearch = startSearch::invoke,
                        focusRequester = focusRequester
                    )
                }

                Divider(
                    thickness = 1.dp,
                    color = dividerColour
                )
            }
        }

        AnimatedVisibility(
            visible = isSearchFocused || isSearchActive,
            enter = expandVertically(animationSpec = tween(400)),
            exit = shrinkVertically(animationSpec = tween(400))
        ) {
            SearchResultsDisplay(
                viewModel = viewModel,
                focusManager = focusManager,
                suggestedSearchList = suggestedSearchList,
                relatedSearchList = relatedSearchList,
                suggestedShow = suggestedShow,
                relatedShow = relatedShow,
                searchListState = searchListState,
                searchApiResponse = searchApiResponse,
            )
        }
    }
}


@Composable
fun SearchBar(
    isSearchFocused: Boolean,
    viewModel: NexusGNViewModel,
    searchBackgroundFocus: Color,
    endSearch: () -> Unit,
    startSearch: () -> Unit,
    focusRequester: FocusRequester
){
    val gameUi by viewModel.uiStateGameDetails.collectAsState()
    val configuration = LocalConfiguration.current

    val searchPhraseBlank by remember {
        derivedStateOf { viewModel.searchPhrase.text.isNotBlank() }
    }
    val searchPhraseIsEmpty by remember {
        derivedStateOf { gameUi.searchPhrase.isNullOrEmpty() }
    }

    val textColour by animateColorAsState(
        targetValue = if(isSearchFocused) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onTertiary,
        animationSpec = tween(200), label = ""
    )

    val animateSize by animateDpAsState(
        targetValue = if (isSearchFocused || searchPhraseBlank || !searchPhraseIsEmpty) configuration.screenWidthDp.dp else 35.dp,
        tween(300), label = ""
    )

    Card(
        modifier = Modifier
            .width(animateSize)
            .height(35.dp),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(searchBackgroundFocus)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AnimatedContent(
                targetState = isSearchFocused || searchPhraseBlank,
                transitionSpec = {
                    fadeIn(tween(400,300)) togetherWith fadeOut()
                }, label = ""
            ) { animateThis ->
                if (animateThis) {
                    Card(
                        modifier = Modifier.padding(6.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onTertiary)
                    ) {
                        Icon(
                            modifier = Modifier.clickable { endSearch() },
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                } else {
                    if(!searchPhraseBlank && searchPhraseIsEmpty){
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    viewModel.apiHandler.closeCard()
                                    focusRequester.requestFocus()
                                }
                                .padding(5.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            BasicTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .onFocusChanged { viewModel.isSearchFocused(it.isFocused) }
                    .focusRequester(focusRequester),
                value = viewModel.searchPhrase,
                onValueChange = { viewModel.update(it) },
                cursorBrush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                ),
                singleLine = true,
                textStyle = TextStyle(
                    color = textColour,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { startSearch() },
                decorationBox = { innerTextField ->
                    if(searchPhraseIsEmpty && !searchPhraseBlank){
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = stringResource(id = R.string.searchGames),
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        gameUi.searchPhrase?.let {
                            SearchBox(
                                searchPhraseBlank = !searchPhraseBlank && !isSearchFocused,
                                gamePhrase = it,
                                continueSearch = {
                                    viewModel.update(
                                        TextFieldValue(gameUi.searchPhrase!!)
                                    )
                                },
                                newSearch = {
                                    viewModel.update(TextFieldValue(""))
                                    viewModel.clearSearchTerm("")
                                    focusRequester.requestFocus()
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun SearchBox(
    searchPhraseBlank: Boolean,
    gamePhrase: String,
    continueSearch:() -> Unit,
    newSearch:() -> Unit,
    viewModel: NexusGNViewModel
){
    AnimatedVisibility(
        visible = searchPhraseBlank,
        enter = expandHorizontally(
            expandFrom = Alignment.Start,
            animationSpec = tween(300)
        ),
        exit = fadeOut(tween(0))
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                viewModel.apiHandler.closeCard()
                                continueSearch()
                            }
                        )
                    }
                    .fillMaxWidth(0.7f)
            ) {
                Text(
                    text = viewModel.stringProvider(R.string.Resume),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = gamePhrase,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(45.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onSecondary)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            viewModel.apiHandler.closeCard()
                            newSearch()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ){
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                        Text(
                            text = viewModel.stringProvider(R.string.New),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconSelection(
    isSearchFocused: Boolean,
    isCardOpen: Boolean,
    isSearchActive: Boolean,
    openDrawer: () -> Unit,
    endSearch: () -> Unit
){
    Crossfade(
        targetState = isSearchFocused || isCardOpen || isSearchActive,
        animationSpec = tween(300), label = ""
    ) { screen ->
        if (screen) {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false,
            ) {
                Column(modifier = Modifier.width(45.dp)) {
                    Card(
                        modifier = Modifier.size(35.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
                        onClick = endSearch::invoke
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxSize(),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.size(35.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
                onClick = openDrawer::invoke
            ) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxSize(),
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SearchBarLogo(
    isSearchFocused: Boolean,
    isCardOpen: Boolean,
    isSearchActive: Boolean
){
    AnimatedVisibility(
        visible = !isSearchFocused && !isCardOpen && !isSearchActive,
        enter = fadeIn(tween(400,300)),
        exit = fadeOut(tween(100))
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
        }
    }
}

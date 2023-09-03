package app.database.nexusgn.ViewModel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import app.database.nexusgn.Data.Api.ApiClassIntegration
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.Implementations.ContextProviderImpl
import app.database.nexusgn.Data.Implementations.RepositoryImpl
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.GamesUiState
import app.database.nexusgn.Data.UiState.InteractionElements
import app.database.nexusgn.Data.UiState.ListUiState
import app.database.nexusgn.Data.UiState.SearchApiResponse
import app.database.nexusgn.Data.Utilities.DateConverter
import app.database.nexusgn.Data.Utilities.IconManager
import app.database.nexusgn.Data.Utilities.rangeFinder
import app.database.nexusgn.R
import app.database.nexusgn.ui.theme.Excellent
import app.database.nexusgn.ui.theme.Mid
import app.database.nexusgn.ui.theme.Skip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedSearch(
    val searchPhrase: String,
    val savedSuggestedResults: List<GameInformation>,
    val savedRelatedResults: List<GameInformation>
)

@HiltViewModel
class NexusGNViewModel @Inject constructor(
    repository: RepositoryImpl,
    val context: ContextProviderImpl,
    savedStateHandle: SavedStateHandle
): ViewModel() {


    val apiHandler = ApiClassIntegration(
        viewModel = this,
        apiImpl = repository
    )

    val listUiStateFlow = MutableStateFlow(ListUiState())
    val uiStateList: StateFlow<ListUiState> = listUiStateFlow.asStateFlow()

    val gameUiStateFlow = MutableStateFlow(GamesUiState())
    val uiStateGameDetails: StateFlow<GamesUiState> = gameUiStateFlow.asStateFlow()

    val interactionUiState = MutableStateFlow(InteractionElements())
    val uiStateInteractionSource: StateFlow<InteractionElements> = interactionUiState.asStateFlow()

    val icons = IconManager(context.getContext())
    val date = DateConverter(this)

    private val savesSearchResults = mutableListOf<SavedSearch>()

    init {
        updateHeader(context.getContext().getString(R.string.Trending))
        updateDate(date.bestRecently())
        updatePage()
        apiHandler.getGames()
        apiHandler.retryRequest()
    }

    @OptIn(SavedStateHandleSaveableApi::class)
    var searchPhrase by savedStateHandle.saveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    fun update(newEntry: TextFieldValue) = viewModelScope.launch{ searchPhrase = newEntry }

    private fun updatePage(){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    pageNumber = 1,
                    pageSize = 40
                )
            }
        }
    }

    fun stringProvider(name: Int, extra: String = ""): String {
        return context.getContext().getString(name,extra)
    }

    fun updateLimitedText(restriction: Boolean){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    isTextRestricted = restriction
                )
            }
        }
    }

    private fun updatePlatforms(platforms: String?){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    platforms = platforms
                )
            }
        }
    }

    private fun updateExclusion(exclusion: Boolean){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    excludeAdditions = exclusion
                )
            }
        }
    }

    private fun updateOrdering(ordering: String?){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    ordering = ordering
                )
            }
        }
    }

    private fun updateSearchTerm(){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    searchPhrase = searchPhrase.text
                )
            }
        }
    }

    fun clearSearchTerm(name: String? = null){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    searchPhrase = name
                )
            }
        }
    }

    private fun updateId(id: Int){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    id = id
                )
            }
        }
    }

    private fun updateInformation(gameInformation: GameInformation){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    gameHolder = gameInformation
                )
            }
        }
    }

    private fun updateHeader(header: String){
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    pageHeader = header
                )
            }
        }
    }

    fun hideSearch(hideSearch: Boolean){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    hideSearch = hideSearch
                )
            }
        }
    }

    fun showScreenshot(boolean: Boolean){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    showScreenshot = boolean
                )
            }
        }
    }

    fun showSelectedImage(currentImage: Int){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    currentImage = currentImage
                )
            }
        }
    }

    private fun updateDate(date: String?) {
        viewModelScope.launch{
            gameUiStateFlow.update { currentDate ->
                currentDate.copy(
                    dateModification = date
                )
            }
        }
    }

    fun isSearchFocused(focused: Boolean){
        viewModelScope.launch {
            interactionUiState.update { updateField ->
                updateField.copy(
                    isSearchFocused = focused
                )
            }
        }
    }

    fun onBackHandler(focusManager: FocusManager) {
        viewModelScope.launch{
            when {
                interactionUiState.value.showScreenshot -> showScreenshot(false)

                uiStateGameDetails.value.searchPhrase?.isNotEmpty() == true || interactionUiState.value.isSearchFocused -> endSearch(focusManager)

                else -> apiHandler.returnToIdle()
            }
        }
    }

    fun checkStringLength(text: String?): Boolean {
        val textLength = text?.split(" ")
        return textLength?.size!! > 50
    }

    fun totalLength(genre: GameInformation): Int{
        var totalLength = 0
        genre.genres?.rangeFinder(3,false)?.forEach {
            totalLength += it.name?.length ?: 0
        }
        return when {
            totalLength <= 16 -> 3
            totalLength <= 20 -> 2
            else -> 1
        }
    }

    fun totalNotShown(gameDetails: GameInformation, minus: Int): Int{
        return gameDetails.genres?.size?.minus(minus)!!
    }

    fun navigateToDetailedView(
        focusManager: FocusManager,
        entries: GameInformation
    ){
        updateId(entries.id!!)
        apiHandler.getGameDetailsAndImages()
        updateInformation(entries)
        focusManager.clearFocus()
        if (apiHandler.gameDetailsApiResponse !is GameDetailsApiResponse.Success) update(TextFieldValue(""))
    }

    fun navigateToPage(
        header: String,
        date: String? = null,
        ordering: String? = null,
        exclusion: Boolean = false,
        platforms: String? = null
    ){
        updateHeader(header)
        updateDate(date)
        updateOrdering(ordering)
        updateExclusion(exclusion)
        updatePlatforms(platforms)
        clearQueries()
        apiHandler.getGames()
    }

    private fun saveSearchResults() {
        val searchPhrase = uiStateGameDetails.value.searchPhrase
        val suggested = uiStateList.value.suggestedSearchResults
        val related = uiStateList.value.relatedSearchResults
        val isSearch = uiStateList.value.savedSearch?.any { it.searchPhrase == searchPhrase } ?: false

        if (!isSearch && !searchPhrase.isNullOrEmpty() && !suggested.isNullOrEmpty()) {
            savesSearchResults.add(
                SavedSearch(
                    searchPhrase = searchPhrase,
                    savedSuggestedResults = suggested,
                    savedRelatedResults = related ?: emptyList()
                )
            )
        }

        listUiStateFlow.update { updateField ->
            updateField.copy(
                suggestedSearchResults = null
            )
        }

        listUiStateFlow.update { updateField ->
            updateField.copy(
                relatedSearchResults = null
            )
        }

        listUiStateFlow.update { updateField ->
            updateField.copy(
                savedSearch = savesSearchResults
            )
        }
    }

    fun gameRatingColourIndication(rating: Int?): Color {
        return when (rating) {
            null -> Color.Transparent
            in 0..49 -> Skip
            in 50..74 -> Mid
            else -> Excellent
        }
    }

    fun usefulLink(context: Context, link: String){
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun startSearch(
        focusManager: FocusManager,
        searchList: LazyListState
    ) {
        viewModelScope.launch {
            if(searchPhrase.text.isNotBlank()){
                if(searchPhrase.text != uiStateGameDetails.value.searchPhrase){
                    apiHandler.returnToIdle()
                    updatePage()
                    updateSearchTerm()
                    apiHandler.allGames.clear()
                    apiHandler.getGamesSearch()
                    focusManager.clearFocus()
                    if(apiHandler.searchApiResponse is SearchApiResponse.Success) searchList.scrollToItem(0)
                }
            }
        }
    }

    private fun clearQueries() {
        apiHandler.allGames.clear()
        updatePage()
        clearSearchTerm()
    }

    fun endSearch(focusManager: FocusManager){
        saveSearchResults()
        focusManager.clearFocus()
        update(TextFieldValue(""))
        clearQueries()
        apiHandler.unsuccessfulSearch()
        apiHandler.returnToIdle()
    }

    fun clearSearch(focusRequester: FocusRequester){
        saveSearchResults()
        focusRequester.requestFocus()
        clearSearchTerm()
        update(TextFieldValue(""))
        apiHandler.unsuccessfulSearch()
    }

    fun nextPage() {
        viewModelScope.launch {
            gameUiStateFlow.update { updateField ->
                updateField.copy(
                    pageNumber = uiStateGameDetails.value.pageNumber?.plus(1)
                )
            }
            if(uiStateGameDetails.value.searchPhrase.isNullOrEmpty()) apiHandler.getGames()
        }
    }

    fun suggestedResults(suggested: List<GameInformation>) {
        listUiStateFlow.update { updateField ->
            updateField.copy(
                suggestedSearchResults = suggested
            )
        }
    }

    fun relatedResults(related: List<GameInformation>) {
        listUiStateFlow.update { updateField ->
            updateField.copy(
                relatedSearchResults = related
            )
        }
    }
    fun updateGenres(genres: String) {
        gameUiStateFlow.update { updateField ->
            updateField.copy(
                genres = genres
            )
        }
    }
}
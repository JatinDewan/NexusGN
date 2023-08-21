package app.database.nexusgn.Data.ApiIntegration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import app.database.nexusgn.Data.ApiDataModel.GameDetails
import app.database.nexusgn.Data.ApiDataModel.GameInformation
import app.database.nexusgn.Data.ApiDataModel.NexusGNData
import app.database.nexusgn.Data.ApiDataModel.Screenshots
import app.database.nexusgn.Data.Implementations.RepositoryImpl
import app.database.nexusgn.Data.UiState.AllGamesApiResponse
import app.database.nexusgn.Data.UiState.GameDetailsApiResponse
import app.database.nexusgn.Data.UiState.ScreenshotsApiResponse
import app.database.nexusgn.Data.UiState.SearchApiResponse
import app.database.nexusgn.ViewModel.NexusGNViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApiClassIntegration(
    private val viewModel: NexusGNViewModel,
    private val apiImpl: RepositoryImpl
) {

    val allGames = mutableListOf<GameInformation>()

    var allGamesApiResponse: AllGamesApiResponse by mutableStateOf(AllGamesApiResponse.Loading)
        private set

    private var searchApiResponse: SearchApiResponse by mutableStateOf(SearchApiResponse.Loading)

    var gameDetailsApiResponse: GameDetailsApiResponse by mutableStateOf(GameDetailsApiResponse.Loading)
        private set

    var screenshotsApiResponse: ScreenshotsApiResponse by mutableStateOf(ScreenshotsApiResponse.Loading)
        private set

    private fun updateResponse(result: NexusGNData){
        allGamesApiResponse = AllGamesApiResponse.Success(result)
    }

    private fun updateSearchResponse(result: NexusGNData){
        searchApiResponse = SearchApiResponse.Success(result)
    }

    private fun updateGameDetails(gameDetails: GameDetails){
        gameDetailsApiResponse = GameDetailsApiResponse.Success(gameDetails)
    }

    private fun updateScreenshotDetail(screenshots: Screenshots){
        screenshotsApiResponse = ScreenshotsApiResponse.Success(screenshots)
    }

    private fun highlightedSearch() {
        try{
            val highlightedMetacritic = allGames.sortedBy { it.metacritic }.last()
            val highlightedRawgRating = allGames.sortedBy { it.ratings_count }.last()

            viewModel.gameUiStateFlow.update { currentState ->
                currentState.copy(
                    highlightedGame = if (highlightedMetacritic.metacritic == null) highlightedRawgRating else highlightedMetacritic
                )
            }
        } catch (e: NoSuchElementException){
            println("this is a test")
        }
    }

    private fun allGamesList(){
        viewModel.listUiStateFlow.update { updateField ->
            updateField.copy(
                allGamesUi = allGames.filter { it != viewModel.uiStateGameDetails.value.highlightedGame }
            )
        }
    }

    private fun updateMutableGamesList(){
        val currentState = allGamesApiResponse
        if(currentState is AllGamesApiResponse.Success) {
            currentState.response

            currentState.response.results.forEach { game ->
                if (!allGames.contains(game) && game.backgroundImage?.isNotEmpty() == true) {
                    allGames.add(game)
                }
            }
        }
    }

    fun retryRequest() {
        viewModel.viewModelScope.launch{
            while (allGamesApiResponse is AllGamesApiResponse.Loading) {
                delay(5000)
                getGames()
            }
        }
    }

    fun getGames(){
        viewModel.viewModelScope.launch{
            try {
                val result = apiImpl.networkCall(
                    page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
                    pageSize = viewModel.uiStateGameDetails.value.pageSize ?: 40,
                    dates = viewModel.uiStateGameDetails.value.dateModification ?: "",
                    ordering = viewModel.uiStateGameDetails.value.ordering ?: "",
                    platforms = viewModel.uiStateGameDetails.value.platforms,
                    excludeAdditions = viewModel.uiStateGameDetails.value.excludeAdditions,
                )
                updateResponse(result)
                updateMutableGamesList()
                highlightedSearch()
                allGamesList()
            } catch (e: Exception) {
                allGamesApiResponse = AllGamesApiResponse.Error(e)
            }
        }
    }

    fun getGameDetails() {
        viewModel.viewModelScope.launch{
            try {
                val gameDetails =
                    apiImpl.networkCallDetails(viewModel.uiStateGameDetails.value.id!!)
                val screenshots =
                    apiImpl.networkCallScreenshots(viewModel.uiStateGameDetails.value.id!!)
                updateGameDetails(
                    gameDetails = gameDetails,
                )
                updateScreenshotDetail(
                    screenshots = screenshots
                )
            } catch (e: Exception) {
                gameDetailsApiResponse = GameDetailsApiResponse.Error(e)
                screenshotsApiResponse = ScreenshotsApiResponse.Error(e)
            }
        }
    }

    private fun sortSearchResults() {
        val searchApiResponse = searchApiResponse
        viewModel.viewModelScope.launch{
            val suggestedList = mutableStateListOf<GameInformation>()
            val relatedList = mutableStateListOf<GameInformation>()

            if(searchApiResponse is SearchApiResponse.Success){
                searchApiResponse.response.results.forEach { game ->
                    if (game.metacritic != null || (game.ratings_count ?: 0) > 0)
                        suggestedList.add(game) else relatedList.add(game)
                }
            }

            viewModel.suggestedResults(
                suggestedList.sortedBy { metacritic -> metacritic.metacritic }.reversed()
                    .sortedBy { ratings -> ratings.ratings_count }.reversed()
            )

            viewModel.relatedResults(relatedList.reversed())
        }
    }

    fun unsuccessfulSearch(){
        val searchApiResponse = searchApiResponse
        viewModel.viewModelScope.launch{
            if (searchApiResponse is SearchApiResponse.Success) {
                viewModel.interactionUiState.update { updateField ->
                    updateField.copy(
                        unsuccessfulSearch =
                        searchApiResponse.response.results.isEmpty() &&
                                (viewModel.uiStateGameDetails.value.searchPhrase?.isNotEmpty() == true)
                    )
                }
            }
        }
    }

    fun getGamesSearch(){
        viewModel.viewModelScope.launch{
            try {
                if (allGames.size <= 200) {

                    val result = apiImpl.networkCallSearch(
                        page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
                        pageSize = viewModel.uiStateGameDetails.value.pageSize ?: 40,
                        search = viewModel.searchPhrase.text,
                        searchPrecise = true,
                        searchExact = false
                    )

                    updateSearchResponse(result)
                    updateMutableGamesList()
                }
                sortSearchResults()
                unsuccessfulSearch()
            } catch (e: Exception) {
                searchApiResponse = SearchApiResponse.Error(e)
            }
        }
    }


}
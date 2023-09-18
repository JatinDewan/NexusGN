package app.database.nexusgn.Data.Api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import app.database.nexusgn.Data.Implementations.ApiImplementation
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
): ApiImplementation {

    val allGames = mutableListOf<GameInformation>()
    var allGamesApiResponse: AllGamesApiResponse by mutableStateOf(AllGamesApiResponse.Loading)
        private set
    var searchApiResponse: SearchApiResponse by mutableStateOf(SearchApiResponse.Idle)
        private set
    var gameDetailsApiResponse: GameDetailsApiResponse by mutableStateOf(GameDetailsApiResponse.Idle)
        private set
    var screenshotsApiResponse: ScreenshotsApiResponse by mutableStateOf(ScreenshotsApiResponse.Idle)
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

    fun returnToIdle() {
        searchApiResponse = SearchApiResponse.Idle
        gameDetailsApiResponse = GameDetailsApiResponse.Idle
        screenshotsApiResponse = ScreenshotsApiResponse.Idle
    }

    fun closeCard() {
        gameDetailsApiResponse = GameDetailsApiResponse.Idle
        screenshotsApiResponse = ScreenshotsApiResponse.Idle
    }

    private fun allGamesList(){
        viewModel.listUiStateFlow.update { updateField ->
            updateField.copy(
                allGamesUi = allGames.filter { game -> game != viewModel.uiStateGameDetails.value.highlightedGame }
            )
        }
    }

    fun retryRequest() {
        viewModel.viewModelScope.launch{
            while (allGamesApiResponse is AllGamesApiResponse.Error) {
                delay(5000)
                getGamesList()
            }
        }
    }

    private fun exceptionChecker(exception: Exception){
        if(!(exception.message == "INITHTTP 404 " && allGames.isNotEmpty())) {
            allGamesApiResponse = AllGamesApiResponse.Error(exception)
        }
    }

    fun unsuccessfulSearch(){
        val searchApiResponseLocal = searchApiResponse
        viewModel.viewModelScope.launch{
            if (searchApiResponseLocal is SearchApiResponse.Success) {
                if(searchApiResponseLocal.response.results.isEmpty()){
                    searchApiResponse = SearchApiResponse.Error(exception = Exception())
                }
            }
        }
    }

    private fun highlightedSearch() {
        val highlightedMetacritic = allGames.sortedBy { it.metacritic }.last()
        val highlightedRawgRating = allGames.sortedBy { it.ratings_count }.last()

        viewModel.gameUiStateFlow.update { currentState ->
            currentState.copy(
                highlightedGame = if (highlightedMetacritic.metacritic == null)
                    highlightedRawgRating else highlightedMetacritic
            )
        }
    }

    private fun sortAllGamesList(){
        val currentState = allGamesApiResponse
        if (currentState is AllGamesApiResponse.Success) {
            allGames.addAll(
                currentState.response.results.filter { game ->
                    !allGames.contains(game) && game.backgroundImage?.isNotEmpty() == true
                }
            )
            highlightedSearch()
            allGamesList()
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

    override fun getSearchedGames() {
        viewModel.viewModelScope.launch{
            searchApiResponse = SearchApiResponse.Loading
            try {
                val result = apiImpl.networkCallSearch(
                    page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
                    pageSize = viewModel.uiStateGameDetails.value.pageSize,
                    search = viewModel.searchPhrase.text,
                    searchPrecise = true,
                    searchExact = false
                )
                updateSearchResponse(result)
                sortSearchResults()
                unsuccessfulSearch()
            } catch (e: Exception) {
                searchApiResponse = SearchApiResponse.Error(e)
            }
        }
    }

    override fun getGamesList() {
        viewModel.viewModelScope.launch{
            allGamesApiResponse = AllGamesApiResponse.Loading
            try {
                val result = apiImpl.networkCall(
                    page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
                    pageSize = viewModel.uiStateGameDetails.value.pageSize,
                    dates = viewModel.uiStateGameDetails.value.dateModification ?: "",
                    ordering = viewModel.uiStateGameDetails.value.ordering ?: "",
                    platforms = viewModel.uiStateGameDetails.value.platforms,
                    excludeAdditions = viewModel.uiStateGameDetails.value.excludeAdditions
                )
                updateResponse(result)
                sortAllGamesList()
            } catch (e: Exception) {
                println(e.message)
                exceptionChecker(e)
            }
        }
    }

    override fun getGamesInformationAndImages() {
        viewModel.viewModelScope.launch{
            gameDetailsApiResponse = GameDetailsApiResponse.Loading
            screenshotsApiResponse = ScreenshotsApiResponse.Loading
            try {
                val gameDetails = apiImpl.networkCallDetails(viewModel.uiStateGameDetails.value.id!!)
                val screenshots = apiImpl.networkCallScreenshots(viewModel.uiStateGameDetails.value.id!!)
                updateGameDetails(gameDetails)
                updateScreenshotDetail(screenshots)
            } catch (e: Exception) {
                gameDetailsApiResponse = GameDetailsApiResponse.Error(e)
                screenshotsApiResponse = ScreenshotsApiResponse.Error(e)
            }
        }
    }

//    fun getGames(){
//        viewModel.viewModelScope.launch{
//            allGamesApiResponse = AllGamesApiResponse.Loading
//            try {
//                val result = apiImpl.networkCall(
//                    page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
//                    pageSize = viewModel.uiStateGameDetails.value.pageSize,
//                    dates = viewModel.uiStateGameDetails.value.dateModification ?: "",
//                    ordering = viewModel.uiStateGameDetails.value.ordering ?: "",
//                    platforms = viewModel.uiStateGameDetails.value.platforms,
//                    excludeAdditions = viewModel.uiStateGameDetails.value.excludeAdditions
//                )
//                updateResponse(result)
//                sortAllGamesList()
//            } catch (e: Exception) {
//                println(e.message)
//                exceptionChecker(e)
//            }
//        }
//    }
//
//    fun getGamesSearch() {
//        viewModel.viewModelScope.launch{
//            searchApiResponse = SearchApiResponse.Loading
//            try {
//                val result = apiImpl.networkCallSearch(
//                    page = viewModel.uiStateGameDetails.value.pageNumber ?: 1,
//                    pageSize = viewModel.uiStateGameDetails.value.pageSize,
//                    search = viewModel.searchPhrase.text,
//                    searchPrecise = true,
//                    searchExact = false
//                )
//                updateSearchResponse(result)
//                sortSearchResults()
//                unsuccessfulSearch()
//            } catch (e: Exception) {
//                searchApiResponse = SearchApiResponse.Error(e)
//            }
//        }
//    }
//
//    fun getGameDetailsAndImages() {
//        viewModel.viewModelScope.launch{
//            gameDetailsApiResponse = GameDetailsApiResponse.Loading
//            screenshotsApiResponse = ScreenshotsApiResponse.Loading
//            try {
//                val gameDetails = apiImpl.networkCallDetails(viewModel.uiStateGameDetails.value.id!!)
//                val screenshots = apiImpl.networkCallScreenshots(viewModel.uiStateGameDetails.value.id!!)
//                updateGameDetails(gameDetails)
//                updateScreenshotDetail(screenshots)
//            } catch (e: Exception) {
//                gameDetailsApiResponse = GameDetailsApiResponse.Error(e)
//                screenshotsApiResponse = ScreenshotsApiResponse.Error(e)
//            }
//        }
//    }

}
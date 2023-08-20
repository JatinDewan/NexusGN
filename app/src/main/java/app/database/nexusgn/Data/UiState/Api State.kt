package app.database.nexusgn.Data.UiState

import app.database.nexusgn.Data.ApiDataModel.GameDetails
import app.database.nexusgn.Data.ApiDataModel.NexusGNData
import app.database.nexusgn.Data.ApiDataModel.Screenshots

sealed interface AllGamesApiResponse {
    data class Success(
        val response: NexusGNData,
    ) : AllGamesApiResponse
    data class Error(val exception: Exception) : AllGamesApiResponse
    data object Loading : AllGamesApiResponse
}

sealed interface SearchApiResponse{
    data class Success(
        val response: NexusGNData,
    ) : SearchApiResponse
    data class Error(val exception: Exception) : SearchApiResponse
    data object Loading : SearchApiResponse
}


sealed interface GameDetailsApiResponse{
    data class Success(
        val gameDetails: GameDetails,
    ) : GameDetailsApiResponse
    data class Error(val exception: Exception) : GameDetailsApiResponse
    data object Loading : GameDetailsApiResponse
}

sealed interface ScreenshotsApiResponse{
    data class Success(
        val screenshots: Screenshots,
    ) : ScreenshotsApiResponse
    data class Error(val exception: Exception) : ScreenshotsApiResponse
    data object Loading : ScreenshotsApiResponse
}
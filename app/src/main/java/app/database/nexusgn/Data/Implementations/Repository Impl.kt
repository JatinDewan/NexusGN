package app.database.nexusgn.Data.Implementations

import app.database.nexusgn.Data.ApiDataModel.GameDetails
import app.database.nexusgn.Data.ApiDataModel.NexusGNData
import app.database.nexusgn.Data.ApiDataModel.Screenshots
import app.database.nexusgn.Data.ApiService.RawgApi

class RepositoryImpl(
    private val api: RawgApi,
) {

    suspend fun networkCallSearch(
        page: Int,
        pageSize: Int,
        search: String,
        searchPrecise: Boolean,
        searchExact: Boolean
    ): NexusGNData {
        return api.getGameSearch(
            page,
            pageSize,
            search,
            searchPrecise,
            searchExact
        )
    }

    suspend fun networkCall(
        page: Int,
        pageSize: Int,
        dates: String,
        ordering: String,
        excludeAdditions: Boolean,
        platforms: String?,
//        genres: String
    ): NexusGNData {
        return api.getGameImageAndData(
            page,
            pageSize,
            dates,
            ordering,
            excludeAdditions,
            platforms,
//            genres
        )
    }

    suspend fun networkCallDetails(id: Int): GameDetails {
        return api.getGameDetails(id)
    }

    suspend fun networkCallScreenshots(id: Int): Screenshots {
        return api.getGameScreenshots(id)
    }

}
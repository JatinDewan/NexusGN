package app.database.nexusgn.Data.Implementations

import app.database.nexusgn.Data.Api.GameDetails
import app.database.nexusgn.Data.Api.NexusGNData
import app.database.nexusgn.Data.Api.Screenshots

class RepositoryImpl(
    private val api: RawgApi,
) {

    suspend fun networkCallSearch(
        page: Int,
        pageSize: Int,
        search: String,
        searchPrecise: Boolean,
        searchExact: Boolean,
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
        platforms: String?
    ): NexusGNData {
        return api.getGameImageAndData(
            page,
            pageSize,
            dates,
            ordering,
            excludeAdditions,
            platforms
        )
    }

    suspend fun networkCallDetails(id: Int): GameDetails = api.getGameDetails(id)

    suspend fun networkCallScreenshots(id: Int): Screenshots = api.getGameScreenshots(id)

}
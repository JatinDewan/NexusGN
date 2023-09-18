package app.database.nexusgn.Data.Implementations

import app.database.nexusgn.Data.Api.GameDetails
import app.database.nexusgn.Data.Api.NexusGNData
import app.database.nexusgn.Data.Api.Screenshots
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val key = "?key=5b9205042d1845988697e1b80120cc2a"

interface RawgApi{


    @GET("games$key")
    suspend fun getGameImageAndData(
        @Query("page")page: Int,
        @Query("page_size")pageSize: Int,
        @Query("dates")dates: String,
        @Query("ordering")ordering: String,
        @Query("exclude_additions")excludeAdditions: Boolean,
        @Query("platforms")platforms: String?
    ): NexusGNData

    @GET("games$key")
    suspend fun getGameSearch(
        @Query("page")page: Int,
        @Query("page_size")pageSize: Int,
        @Query("search")search: String,
        @Query("search_precise")searchPrecise: Boolean,
        @Query("search_exact")searchExact: Boolean
    ): NexusGNData

    @GET("games/{id}$key")
    suspend fun getGameDetails(
        @Path("id")id: Int,
    ): GameDetails

    @GET("games/{id}/screenshots$key")
    suspend fun getGameScreenshots(
        @Path("id")id: Int,
    ): Screenshots

}
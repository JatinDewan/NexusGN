package app.database.nexusgn.Data.ApiDataModel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatformDetails(
    val id: Int? = null,
    val name: String? = null,
    @SerialName(value = "games_count")
    val gamesCount: Int? = null,
    @SerialName(value = "image_background")
    val imageBackground: String?,
    val image: String? = null,
    @SerialName(value = "year_start")
    val yearStart: Int? = null,
    @SerialName(value = "year_end")
    val yearEnd: Int? = null
)

@Serializable
data class PlatformList(
    val count: Int? = null,
    val results: List<PlatformDetails>
)

@Serializable
data class AddedStatus(
    val yet: Int? = null,
    val owned: Int? = null,
    val beaten: Int? = null,
    @SerialName(value = "toplay")
    val toPlay: Int? = null,
    val dropped: Int? = null,
    val playing: Int? = null
)

@Serializable
data class Ratings(
    val id:Int? = null,
    val title: String? = null,
    var count: Int? = null,
    val percent: Float
)

@Serializable
data class Requirements(
    val recommended: String? = null,
    val minimum: String? = null,
)

@Serializable
data class Platform(
    val id: Int? = null,
    val name: String? = null,
    val slug: String? = null,
    val image: String? = null,
    @SerialName(value = "year_end")
    val yearEnd: Int? = null,
    @SerialName(value = "year_start")
    val yearStart: Int? = null,
    @SerialName(value = "games_count")
    val gamesCount: Int? = null,
    @SerialName(value = "image_background")
    val imageBackground: String? = null,
)

@Serializable
data class Platforms(
    val platform: Platform? = null,
    @SerialName(value = "released_at")
    val releasedAt: String? = null,
    @SerialName(value = "requirements_en")
    val requirementsEN: Requirements? = null,
    @SerialName(value = "requirements_ru")
    val requirementsRU: Requirements? = null
)

@Serializable
data class ESRBRating(
    val id: Int? = null,
    val slug: String? = null,
    val name: String? = null
)

@Serializable
data class GameInformation(
    val id: Int? = null,
    val slug: String? = null,
    val name: String? = null,
    val released: String? = null,
    val metacritic: Int?,
    val platforms: List<Platforms>? = null,
    @SerialName(value = "background_image")
    val backgroundImage: String? = null,
    @SerialName(value = "ratings_count")
    val ratingsCount: Int? = null,
    val genres: List<Platform>? = null,
)

@Serializable
data class NexusGNData(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<GameInformation>
)

@Serializable
data class GameDetails(
    val description: String? = null,
    val metacritic: Int? = null,
    val updated: String? = null,
    val website: String? = null,
    val rating: Float? = null,
    val ratings: List<Ratings>? = null,
    @SerialName(value = "rating_top")
    val ratingTop: Int? = null,
    val playtime: Int? = null,
    @SerialName(value = "reddit_url")
    val redditUrl: String? = null,
    @SerialName(value = "reddit_name")
    val redditName: String? = null,
    @SerialName(value = "metacritic_url")
    val metacriticUrl: String? = null,
    @SerialName(value = "esrb_rating")
    val esrbRating: ESRBRating? = null,
    val platforms: List<Platform>? = null,
    @SerialName(value = "description_raw")
    val rawDescription: String? = null
)

@Serializable
data class Images(
    val id: Int? = null,
    val image: String? = null
)

@Serializable
data class Screenshots(
    val count: Int,
    val results: List<Images>
)
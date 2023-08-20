package app.database.nexusgn.Data.UiState

import app.database.nexusgn.Data.ApiDataModel.GameInformation
import app.database.nexusgn.ViewModel.SavedSearch

data class ListUiState(

    val allGamesUi: List<GameInformation>? = null,
    val suggestedSearchResults: List<GameInformation>? = null,
    val relatedSearchResults: List<GameInformation>? = null,
    val savedSearch: List<SavedSearch>? = null,

    )

data class GamesUiState(

    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val id: Int? = null,
    val ordering: String? = null,
    val searchPhrase: String? = null,
    val dateModification: String? = null,
    val pageHeader: String? = null,
    val excludeAdditions: Boolean? = null,
    val platforms: String? = null,
    val highlightedGame: GameInformation? = null,
    val genres: String? = null

)

data class InteractionElements(

    val showScreenshot: Boolean = false,
    val currentImage: Int? = null,
    val gameHolder: GameInformation? = null,
    val gameHolderIndex: Int? = null,
    val hideSearch: Boolean = false,
    val clickedCard: Boolean = false,
    val noMorePages: Int? = null,
    val isTextRestricted: Boolean = false,
    val isSearchFocused: Boolean = false,
    val unsuccessfulSearch: Boolean = false

)
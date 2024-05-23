package work.wander.wikiview.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import work.wander.wikiview.framework.annotation.BackgroundThread
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaMobileHtmlService
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaSearchService
import javax.inject.Inject


@Serializable
data class SearchResultItem(
    val id: Long,
    val key: String,
    val title: String,
    val description: String,
    val thumbnailImageUrl: String? = null
)

sealed interface HomeSearchUiState {
    object Initial : HomeSearchUiState
    data class Loading(val query: String) : HomeSearchUiState
    data class Success(val results: List<SearchResultItem>) : HomeSearchUiState
    data class Error(val message: String) : HomeSearchUiState
}

@Serializable
data class ItemDetailContents(
    val pageId: Long,
    val key: String,
    val mobileHtml: String,
)

sealed interface HomeDetailUiState {
    object Initial : HomeDetailUiState
    data class Loading(val pageTitle: String) : HomeDetailUiState
    data class Success(
        val pageTitle: String,
        val pageContents: String
    ) : HomeDetailUiState

    data class Error(val message: String) : HomeDetailUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wikipediaSearchService: WikipediaSearchService,
    private val wikipediaMobileHtmlService: WikipediaMobileHtmlService,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
) : ViewModel() {

    private val currentSearchResults =
        MutableStateFlow<HomeSearchUiState>(HomeSearchUiState.Initial)

    fun searchUiState(): StateFlow<HomeSearchUiState> = currentSearchResults

    private val currentDetailPane =
        MutableStateFlow<HomeDetailUiState>(HomeDetailUiState.Initial)

    fun detailUiState(): StateFlow<HomeDetailUiState> = currentDetailPane

    fun search(query: String) {
        viewModelScope.launch(backgroundDispatcher) {
            currentSearchResults.update {
                HomeSearchUiState.Loading(query)
            }
            val response = wikipediaSearchService.search(query)
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    appLogger.error("Empty response body")
                    currentSearchResults.update {
                        HomeSearchUiState.Error("Empty Response")
                    }
                } else if (body.pages.isEmpty()) {
                    appLogger.debug("No search results found")
                    currentSearchResults.update {
                        HomeSearchUiState.Success(emptyList())
                    }
                } else {
                    appLogger.debug("Search results: ${body.pages}")
                    currentSearchResults.update {
                        HomeSearchUiState.Success(body.pages.map { searchResult ->
                            SearchResultItem(
                                id = searchResult.id,
                                key = searchResult.key,
                                title = searchResult.title,
                                description = searchResult.description ?: "",
                                thumbnailImageUrl = searchResult.thumbnail?.url,
                            )
                        })
                    }
                }
            } else {
                appLogger.error("Failed to search Wikipedia: ${response.errorBody()}")
                currentSearchResults.update {
                    HomeSearchUiState.Error("Unable to retrieve results from Wikipedia API")
                }
            }
        }
    }

    fun setDetailPanePageTitle(pageTitle: String) {
        viewModelScope.launch(backgroundDispatcher) {
            currentDetailPane.update {
                HomeDetailUiState.Loading(pageTitle)
            }
            val response = wikipediaMobileHtmlService.getMobileHtml(pageTitle)
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    appLogger.error("Empty response body")
                    currentDetailPane.update {
                        HomeDetailUiState.Error("Empty Response")
                    }
                } else {
                    appLogger.debug("Mobile HTML Successfully Retrieved for: $pageTitle")
                    currentDetailPane.update {
                        HomeDetailUiState.Success(pageTitle, body)
                    }
                }
            } else {
                appLogger.error("Failed to retrieve mobile HTML: ${response.errorBody()}")
                currentDetailPane.update {
                    HomeDetailUiState.Error("Unable to retrieve mobile HTML from Wikipedia API")
                }
            }
        }
    }

}
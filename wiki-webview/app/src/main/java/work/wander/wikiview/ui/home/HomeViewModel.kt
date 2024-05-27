package work.wander.wikiview.ui.home

import android.os.Parcelable
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import work.wander.wikiview.domain.wiki.page.WikipediaPage
import work.wander.wikiview.domain.wiki.search.WikipediaSearch
import work.wander.wikiview.framework.annotation.BackgroundThread
import work.wander.wikiview.framework.logging.AppLogger
import javax.inject.Inject


@Parcelize
data class SearchResultItem(
    val wikiPageId: Long,
    val key: String,
    val title: String,
    val description: String,
    val thumbnailImageUrl: String? = null
) : Parcelable

sealed interface HomeSearchUiState {
    object Initial : HomeSearchUiState
    data class Loading(val query: String) : HomeSearchUiState
    data class Success(
        val searchQuery: String,
        val results: List<SearchResultItem>
    ) : HomeSearchUiState

    data class Error(val message: String) : HomeSearchUiState
}

sealed interface HomeDetailUiState {
    object Initial : HomeDetailUiState
    data class Loading(val pageTitle: String) : HomeDetailUiState
    data class Success(
        val pageTitle: String,
        val pageDescription: String,
        val thumbnailImageUrl: String?,
        val mobileHtmlContent: String,
        val defaultHtmlContent: String,
        val webViewClient: WebViewClient? = null
    ) : HomeDetailUiState

    data class Error(val message: String) : HomeDetailUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wikipediaSearch: WikipediaSearch,
    private val wikipediaPage: WikipediaPage,
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
        currentSearchResults.update {
            HomeSearchUiState.Loading(query)
        }
        viewModelScope.launch(backgroundDispatcher) {
            when (val response = wikipediaSearch.searchForPages(query)) {
                is WikipediaSearch.SearchResponse.Success -> {
                    currentSearchResults.update {
                        HomeSearchUiState.Success(
                            searchQuery = query,
                            results = response.pages.map { searchResult ->
                                SearchResultItem(
                                    wikiPageId = searchResult.wikiPageId,
                                    key = searchResult.key,
                                    title = searchResult.title,
                                    description = searchResult.description,
                                    thumbnailImageUrl = searchResult.url
                                )
                            })
                    }
                }

                is WikipediaSearch.SearchResponse.Error -> {
                    appLogger.error("Failed to search Wikipedia for query: $query")
                    currentSearchResults.update {
                        HomeSearchUiState.Error(response.message)
                    }
                }
            }
        }
    }

    fun setDetailPanePageTitle(pageTitle: String) {
        viewModelScope.launch(backgroundDispatcher) {
            currentDetailPane.update {
                HomeDetailUiState.Loading(pageTitle)
            }
            val defaultHtml = wikipediaPage.getDefaultHtmlForPage(pageTitle)
            val mobileHtml = wikipediaPage.getMobileHtmlForPage(pageTitle)
            if (defaultHtml != null && mobileHtml != null) {
                currentDetailPane.update {
                    HomeDetailUiState.Success(
                        pageTitle = pageTitle,
                        pageDescription = defaultHtml.html,
                        thumbnailImageUrl = mobileHtml.html,
                        mobileHtmlContent = mobileHtml.getBase64EncodedHtml(),
                        defaultHtmlContent = defaultHtml.getBase64EncodedHtml(),
                        webViewClient = webViewClient
                    )
                }
            } else {
                appLogger.error("Failed to fetch HTML for page $pageTitle")
                currentDetailPane.update {
                    HomeDetailUiState.Error("Failed to fetch HTML for page $pageTitle")
                }
            }
        }
    }

    private val webViewClient = object : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            appLogger.info("Finished loading page: $url")
            super.onPageFinished(view, url)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            appLogger.info("Loading resource: $url")
            super.onLoadResource(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            appLogger.error("Received error: ${error?.description}")
            super.onReceivedError(view, request, error)
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            appLogger.info("Should Override Key Event: ${event?.keyCode}")
            return super.shouldOverrideKeyEvent(view, event)
        }

        override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
            appLogger.info("On Unhandled Key Event: ${event?.keyCode}")
            super.onUnhandledKeyEvent(view, event)
        }

        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            appLogger.info("Scale changed from $oldScale to $newScale")
            super.onScaleChanged(view, oldScale, newScale)
        }

    }

}
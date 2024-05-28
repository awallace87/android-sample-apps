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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import work.wander.wikiview.data.settings.ApplicationSettingsRepository
import work.wander.wikiview.domain.wiki.page.WikipediaPage
import work.wander.wikiview.domain.wiki.search.WikipediaSearch
import work.wander.wikiview.framework.annotation.BackgroundThread
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.proto.settings.WikipediaViewSettings
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
    data object Initial : HomeSearchUiState
    data class Loading(val query: String) : HomeSearchUiState
    data class Success(
        val searchQuery: String,
        val results: List<SearchResultItem>
    ) : HomeSearchUiState

    data class Error(val message: String) : HomeSearchUiState
}

sealed interface HomeDetailUiState {
    data object Initial : HomeDetailUiState
    data class Loading(val pageTitle: String) : HomeDetailUiState
    data class Success(
        val pageTitle: String,
        val htmlContent: String,
        val webViewClient: WebViewClient? = null
    ) : HomeDetailUiState

    data class Error(val message: String) : HomeDetailUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wikipediaSearch: WikipediaSearch,
    private val wikipediaPage: WikipediaPage,
    private val applicationSettingsRepository: ApplicationSettingsRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
) : ViewModel() {

    private val currentSearchResults =
        MutableStateFlow<HomeSearchUiState>(HomeSearchUiState.Initial)

    private val currentDetailPane =
        MutableStateFlow<HomeDetailUiState>(HomeDetailUiState.Initial)

    private val wikipediaViewSettings =
        applicationSettingsRepository.getApplicationSettings().map { settings ->
            settings.wikipediaViewSettings
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            WikipediaViewSettings.getDefaultInstance()
        )

    fun searchUiState(): StateFlow<HomeSearchUiState> = currentSearchResults

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

            val selectedHtmlContentType = wikipediaViewSettings.value.selectedHtmlType
            val html = when (selectedHtmlContentType) {
                WikipediaViewSettings.PageHtmlType.MOBILE -> wikipediaPage.getMobileHtmlForPage(
                    pageTitle
                )?.getBase64EncodedHtml()

                WikipediaViewSettings.PageHtmlType.DEFAULT -> wikipediaPage.getDefaultHtmlForPage(
                    pageTitle
                )?.getBase64EncodedHtml()

                else -> {
                    appLogger.error("Unrecognized HTML Type: $selectedHtmlContentType (Defaulting to Mobile)")
                    wikipediaPage.getMobileHtmlForPage(pageTitle)?.getBase64EncodedHtml()
                }
            }

            currentDetailPane.update {
                HomeDetailUiState.Success(
                    pageTitle = pageTitle,
                    htmlContent = html ?: "",
                    webViewClient = webViewClient
                )
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
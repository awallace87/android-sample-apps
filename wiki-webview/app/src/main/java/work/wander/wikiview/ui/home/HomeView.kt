package work.wander.wikiview.ui.home

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import work.wander.wikiview.R
import work.wander.wikiview.ui.common.LoadingIndicatorView
import work.wander.wikiview.ui.common.PicassoImage
import work.wander.wikiview.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeView(
    searchListUiState: StateFlow<HomeSearchUiState>,
    detailUiState: StateFlow<HomeDetailUiState>,
    modifier: Modifier = Modifier,
    onSearchRequested: (String) -> Unit = {},
    onSearchResultSelected: (SearchResultItem) -> Unit = {},
    onInternalLinkSelected: (String) -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<SearchResultItem>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                val searchState = searchListUiState.collectAsState().value
                HomeListPaneContents(
                    searchUiState = searchState,
                    onResultSelected = {
                        onSearchResultSelected(it)
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, it)
                    },
                    onSearchRequested = onSearchRequested,
                    onSettingsSelected = onSettingsSelected
                )
            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.content?.let { searchResult ->
                    HomeDetailPaneContents(
                        detailUiState = detailUiState,
                        selectedSearchResult = searchResult,
                        onInternalLinkClicked = onInternalLinkSelected,
                    )
                } ?: Text(text = "Detail destination not selected")
            }
        },
    )
}

@Composable
fun HomeListPaneContents(
    searchUiState: HomeSearchUiState,
    modifier: Modifier = Modifier,
    onResultSelected: (SearchResultItem) -> Unit = {},
    onSearchRequested: (String) -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val searchFieldLabel = when (searchUiState) {
            is HomeSearchUiState.Success -> searchUiState.searchQuery
            else -> ""
        }
        HomeTopAppBar(
            searchFieldLabel = searchFieldLabel,
            modifier = Modifier
                .fillMaxWidth(),
            onSearchRequested = onSearchRequested,
            onSettingsSelected = onSettingsSelected
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (searchUiState) {
                is HomeSearchUiState.Success -> {
                    val searchResults = searchUiState.results
                    if (searchResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "No search results",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(searchResults.size) { searchResultIndex ->
                                val searchResult = searchResults[searchResultIndex]
                                HomeSearchResultItemView(
                                    searchResult = searchResult,
                                    modifier = Modifier.fillMaxWidth(),
                                    onSelected = {
                                        onResultSelected(searchResult)
                                    }
                                )
                                if (searchResultIndex < searchResults.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                is HomeSearchUiState.Error -> {
                    Text(
                        "Error loading search results: ${searchUiState.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                HomeSearchUiState.Initial -> {
                    Text(
                        "Search for articles on Wikipedia",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                is HomeSearchUiState.Loading -> {
                    LoadingIndicatorView(
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }


    }
}

@Composable
fun HomeSearchResultItemView(
    searchResult: SearchResultItem,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        val headerImageSize = 36.dp
        if (searchResult.thumbnailImageUrl != null) {
            PicassoImage(
                url = searchResult.thumbnailImageUrl,
                modifier = Modifier.size(headerImageSize)
            )
        } else {
            Icon(
                Icons.Outlined.ImageNotSupported,
                modifier = Modifier.size(headerImageSize),
                contentDescription = "Thumbnail",
                tint = MaterialTheme.colorScheme.primary
            )

        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            searchResult.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            searchResult.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.5f)
        )
        IconButton(onClick = {
            onSelected()
        }) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                modifier = Modifier.size(32.dp),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

// TODO: Find more proper way to avoid re-composition of the same page contents
@Composable
fun HomeDetailPaneContents(
    selectedSearchResult: SearchResultItem,
    detailUiState: StateFlow<HomeDetailUiState>,
    modifier: Modifier = Modifier,
    onInternalLinkClicked: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val detailState = detailUiState.collectAsState().value) {
            is HomeDetailUiState.Initial -> {
                Text(
                    "Select a search result to view details",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            is HomeDetailUiState.Loading -> {
                Text(
                    "Loading details for ${detailState.pageTitle}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            is HomeDetailUiState.Success -> {
                WikiPageDetailsView(
                    detailState,
                    modifier = Modifier.fillMaxSize(),
                    onInternalLinkClicked = onInternalLinkClicked
                )
            }

            is HomeDetailUiState.Error -> {
                Text(
                    "Error loading details: ${detailState.message}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun WikiPageDetailsView(
    successState: HomeDetailUiState.Success,
    modifier: Modifier = Modifier,
    onInternalLinkClicked: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(
            "Article: ${successState.pageTitle}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        val html = successState.htmlContent

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        if (successState.webViewClient != null) {
                            webViewClient = successState.webViewClient
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        // TODO: Should allow for "Desktop" viewing mode
                        settings.useWideViewPort = false

                        addJavascriptInterface(object {
                            @android.webkit.JavascriptInterface
                            fun onLinkClick(url: String) {
                                onInternalLinkClicked(url)
                            }
                        }, "Android")

                        // Loading Base64 encoded HTML content
                        // See(https://developer.android.com/develop/ui/views/layout/webapps/load-local-content)
                        loadData(html, "text/html", "base64")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    searchFieldLabel: String,
    modifier: Modifier = Modifier,
    onSearchRequested: (String) -> Unit = {},
    onSettingsSelected: () -> Unit = {}
) {
    TopAppBar(
        title = {
            HomeSearchInput(
                searchFieldLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 4.dp),
                onSearchRequested = onSearchRequested
            )
        },
        modifier = modifier
            .size(96.dp)
            .clip(MaterialTheme.shapes.small),
        colors = TopAppBarDefaults.mediumTopAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
            Box(modifier = Modifier.fillMaxHeight()) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(52.dp)
                        .padding(start = 4.dp)
                        .align(Alignment.Center)
                        .clip(MaterialTheme.shapes.small)
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onSettingsSelected() },
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Outlined.Settings,
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}


@Composable
fun HomeSearchInput(
    searchFieldLabel: String,
    modifier: Modifier = Modifier, onSearchRequested: (String) -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = FocusRequester()
        val searchQuery = rememberSaveable { mutableStateOf(searchFieldLabel) }

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search", style = MaterialTheme.typography.labelLarge) },
            textStyle = MaterialTheme.typography.labelLarge,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Ascii
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                    onSearchRequested(searchQuery.value)
                },
            ),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .padding(end = 8.dp),
        )
    }
}


@Preview
@Composable
private fun HomeViewPreview() {
    AppTheme {
        val testDetailUiState = MutableStateFlow(HomeDetailUiState.Initial)
        HomeView(
            searchListUiState = MutableStateFlow(HomeSearchUiState.Initial),
            detailUiState = testDetailUiState
        )
    }
}

@Preview
@Composable
private fun HomeListPaneContentsPreview() {
    AppTheme {
        val uiState = MutableStateFlow(HomeSearchUiState.Initial)
        HomeListPaneContents(
            searchUiState = HomeSearchUiState.Initial,
            onResultSelected = {},
        )
    }
}

@Preview
@Composable
private fun HomeDetailPaneContentsPreview() {
    AppTheme {
        val searchResultItem = SearchResultItem(
            wikiPageId = 1L,
            key = "test_key",
            title = "Title",
            description = "Description",
            thumbnailImageUrl = "https://example.com/image.jpg",
        )

        val initialState = MutableStateFlow(HomeDetailUiState.Initial)

        val loadingState = MutableStateFlow(HomeDetailUiState.Loading("Title"))
        val successState = MutableStateFlow(
            HomeDetailUiState.Success(
                pageTitle = "Title",
                htmlContent = "<html><body><h1>Test</h1></body></html>",
            )
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            HomeDetailPaneContents(
                selectedSearchResult = searchResultItem,
                detailUiState = initialState
            )
            HorizontalDivider(
                modifier = Modifier.padding(8.dp)
            )
            HomeDetailPaneContents(
                searchResultItem, loadingState
            )
            HorizontalDivider(
                modifier = Modifier.padding(8.dp)
            )
            HomeDetailPaneContents(
                searchResultItem, successState
            )
            HorizontalDivider(
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    AppTheme {
        HomeTopAppBar(
            searchFieldLabel = "Search",
            modifier = Modifier
                .fillMaxWidth()
                .size(72.dp)
        )
    }
}

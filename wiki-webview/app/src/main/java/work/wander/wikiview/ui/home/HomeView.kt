package work.wander.wikiview.ui.home

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import work.wander.wikiview.R
import work.wander.wikiview.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeView(
    searchResults: List<SearchResultItem>,
    detailUiState: StateFlow<HomeDetailUiState>,
    modifier: Modifier = Modifier,
    onSearchRequested: (String) -> Unit = {},
    onSearchResultSelected: (SearchResultItem) -> Unit = {},
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
                HomeListPaneContents(
                    searchResults = searchResults,
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
                        selectedSearchResult = searchResult
                    )
                } ?: Text(text = "Detail destination not selected")
            }
        },
    )
}

@Composable
fun HomeListPaneContents(
    searchResults: List<SearchResultItem>,
    modifier: Modifier = Modifier,
    onResultSelected: (SearchResultItem) -> Unit = {},
    onSearchRequested: (String) -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeTopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            onSearchRequested = onSearchRequested,
            onSettingsSelected = onSettingsSelected
        )
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
        val headerImageSize = 48.dp
        if (searchResult.thumbnailImageUrl != null) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "Thumbnail",
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
        Text(
            searchResult.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            searchResult.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
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
    }
}

// TODO: Find more proper way to avoid re-composition of the same page contents
@Composable
fun HomeDetailPaneContents(
    selectedSearchResult: SearchResultItem,
    detailUiState: StateFlow<HomeDetailUiState>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val detailState = detailUiState.collectAsState().value
        when (detailState) {
            is HomeDetailUiState.Initial -> {
                Text("Select a search result to view details")
            }

            is HomeDetailUiState.Loading -> {
                Text("Loading details for ${selectedSearchResult.title}")
            }

            is HomeDetailUiState.Success -> {
                Text(detailState.pageContents)
            }

            is HomeDetailUiState.Error -> {
                Text("Error loading details: ${detailState.message}")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onSearchRequested: (String) -> Unit = {},
    onSettingsSelected: () -> Unit = {}
) {
    TopAppBar(
        title = {
            HomeSearchInput(
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
                    Icons.Outlined.MoreVert,
                    modifier = Modifier
                        .size(36.dp),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}


@Composable
fun HomeSearchInput(modifier: Modifier = Modifier, onSearchRequested: (String) -> Unit = {}) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = FocusRequester()
        val searchQuery = remember { mutableStateOf("") }

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
            searchResults = listOf(
            ),
            detailUiState = testDetailUiState,
        )
    }
}

@Preview
@Composable
private fun HomeListPaneContentsPreview() {
    AppTheme {
        HomeListPaneContents(
            searchResults = listOf(
                SearchResultItem(
                    id = 1L,
                    key = "test_key",
                    title = "Title",
                    description = "Description",
                    thumbnailImageUrl = "https://example.com/image.jpg",
                ),
                SearchResultItem(
                    id = 2L,
                    key = "test_key",
                    title = "Title",
                    description = "Description",
                    thumbnailImageUrl = "https://example.com/image.jpg",
                ),
                SearchResultItem(
                    id = 3L,
                    key = "test_key",
                    title = "Title",
                    description = "Description",
                    thumbnailImageUrl = "https://example.com/image.jpg",
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun HomeDetailPaneContentsPreview() {
    AppTheme {
        val testDetailUiState = MutableStateFlow(HomeDetailUiState.Initial)

        HomeDetailPaneContents(
            SearchResultItem(
                id = 1L,
                key = "test_key",
                title = "Title",
                description = "Description",
                thumbnailImageUrl = "https://example.com/image.jpg",
            ), testDetailUiState
        )
    }
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    AppTheme {
        HomeTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .size(72.dp)
        )
    }
}

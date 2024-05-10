package work.wander.directory.ui.directory

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import work.wander.directory.R
import work.wander.directory.data.employee.room.EmployeeType
import work.wander.directory.ui.common.LoadingIndicatorView
import work.wander.directory.ui.common.PicassoImage
import work.wander.directory.ui.theme.AppTheme
import java.util.UUID

/**
 * Composable function to display the directory screen.
 *
 * @param uiState The UI state of the directory screen. This can be Loading, Error, or Success.
 * @param isRefreshingData A state representing whether the data is currently being refreshed.
 * @param modifier The modifier to be applied to the Scaffold.
 * @param onRefreshRequested A callback that's invoked when a refresh is requested.
 * @param onEmployeeSelected A callback that's invoked when an employee is selected. The employee's ID is passed as a parameter.
 * @param refreshOnStart A boolean indicating whether a refresh should be requested when the composable is first composed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DirectoryScreenView(
    uiState: DirectoryScreenUiState,
    isRefreshingData: State<Boolean>,
    modifier: Modifier = Modifier,
    onRefreshRequested: () -> Unit = {},
    onEmployeeSelected: (String) -> Unit = {},
    refreshOnStart: Boolean = false,
    onSettingsSelected: () -> Unit = {},
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshingData.value,
        onRefresh = { onRefreshRequested() }
    )

    val isInitialCompose = remember { mutableStateOf(true) }

    if (refreshOnStart && isInitialCompose.value) {
        isInitialCompose.value = false
        LaunchedEffect(Unit) {
            delay(100)
            onRefreshRequested()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = {
                    Text(
                        "Employee Directory",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                },
                navigationIcon = {
                    val image = painterResource(id = R.drawable.directory_logo)
                    Image(
                        painter = image,
                        contentDescription = "Company Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(start = 4.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onSettingsSelected() },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            )
        },
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(it)
                .pullRefresh(pullRefreshState)
        ) {
            val hasItemsDisplayed = uiState is DirectoryScreenUiState.Success && uiState.employees.isNotEmpty()
            if (isRefreshingData.value && !hasItemsDisplayed) {
                LoadingIndicatorView(
                    contentDescription = "Refreshing Directory",
                    modifier = Modifier
                        .width(96.dp)
                        .align(Alignment.Center)
                )
            } else {
                DirectoryScreenContents(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    onEmployeeSelected = onEmployeeSelected,
                    onRefreshRequested = onRefreshRequested,
                )
                PullRefreshIndicator(
                    refreshing = isRefreshingData.value,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = if (isRefreshingData.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }

}

/**
 * Composable function to display the contents of the directory screen.
 *
 * @param uiState The UI state of the directory screen. This can be Loading, Error, or Success.
 * @param modifier The modifier to be applied to the Column.
 * @param onEmployeeSelected A callback that's invoked when an employee is selected. The employee's ID is passed as a parameter.
 */
@Composable
fun DirectoryScreenContents(
    uiState: DirectoryScreenUiState,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (String) -> Unit = {},
    onRefreshRequested: () -> Unit = {},
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            is DirectoryScreenUiState.Loading -> {
                LoadingIndicatorView(
                    contentDescription = "Loading Directory",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            is DirectoryScreenUiState.Error -> {
                Text(text = "Error: ${uiState.message}")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = { onRefreshRequested() }) {
                    Text("Refresh from server")
                }
            }

            is DirectoryScreenUiState.Success -> {
                if (uiState.employees.isEmpty()) {
                    Text(
                        text = "No employees found",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = { onRefreshRequested() }) {
                        Text("Refresh from server")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.employees.size) { employee ->
                            val employeeData = uiState.employees[employee]
                            EmployeeRow(
                                employee = employeeData,
                                onEmployeeSelected = onEmployeeSelected
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * Composable function to display a row for an employee.
 *
 * @param employee The data for the employee to be displayed.
 * @param onEmployeeSelected A callback that's invoked when the row is clicked. The employee's ID is passed as a parameter.
 */
@Composable
fun EmployeeRow(
    employee: EmployeeRowData,
    onEmployeeSelected: (String) -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 4.dp,
                    top = 8.dp,
                    bottom = 4.dp,
                )
                .clickable {
                    onEmployeeSelected(employee.id)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            PicassoImage(
                url = employee.photoUrlSmall,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = employee.fullName,
                    modifier = Modifier.weight(0.4f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                    maxLines = 2
                )
                Text(
                    text = employee.teamText,
                    modifier = Modifier
                        .weight(0.3f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = employee.employeeTypeText,
                    modifier = Modifier
                        .weight(0.25f)
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}


@Preview
@Composable
fun DirectoryScreenViewPreview() {
    AppTheme {
        val isRefreshing = remember { mutableStateOf(false) }
        DirectoryScreenView(
            onRefreshRequested = {},
            onEmployeeSelected = {},
            uiState = DirectoryScreenUiState.Loading,
            isRefreshingData = isRefreshing
        )
    }
}

@Preview
@Composable
fun DirectoryScreenContentsPreview() {
    AppTheme {
        DirectoryScreenContents(
            uiState = DirectoryScreenUiState.Loading
        )
    }
}

@Preview
@Composable
fun DirectoryScreenContentsErrorPreview() {
    AppTheme {
        DirectoryScreenContents(
            uiState = DirectoryScreenUiState.Error("An error occurred")
        )
    }
}

@Preview
@Composable
fun DirectoryScreenContentsSuccessEmptyPreview() {
    AppTheme {
        DirectoryScreenContents(
            uiState = DirectoryScreenUiState.Success(
                employees = emptyList()
            )
        )
    }
}

@Preview
@Composable
fun DirectoryScreenContentsSuccessPreview() {
    AppTheme {
        DirectoryScreenContents(
            uiState = DirectoryScreenUiState.Success(
                employees = listOf(
                    EmployeeRowData(
                        id = UUID.randomUUID().toString(),
                        fullName = "John Doe",
                        teamText = "Engineering",
                        employeeTypeText = EmployeeType.FULL_TIME.displayString,
                        photoUrlSmall = "https://www.example.com/photo.jpg"
                    ),
                    EmployeeRowData(
                        id = UUID.randomUUID().toString(),
                        fullName = "Jane Doe",
                        teamText = "Engineering",
                        employeeTypeText = EmployeeType.PART_TIME.displayString,
                        photoUrlSmall = "https://www.example.com/photo.jpg"
                    ),
                )
            )
        )
    }
}

@Preview
@Composable
fun DirectoryScreenContentsSuccessDarkPreview() {
    AppTheme(darkTheme = true) {
        DirectoryScreenContents(
            uiState = DirectoryScreenUiState.Success(
                employees = listOf(
                    EmployeeRowData(
                        id = UUID.randomUUID().toString(),
                        fullName = "John Doe",
                        teamText = "Engineering",
                        employeeTypeText = EmployeeType.FULL_TIME.displayString,
                        photoUrlSmall = "https://www.example.com/photo.jpg"
                    ),
                    EmployeeRowData(
                        id = UUID.randomUUID().toString(),
                        fullName = "Jane Doe",
                        teamText = "Engineering",
                        employeeTypeText = EmployeeType.PART_TIME.displayString,
                        photoUrlSmall = "https://www.example.com/photo.jpg"
                    ),
                )
            )
        )
    }
}

@Preview
@Composable
fun EmployeeRowPreview() {
    AppTheme {
        EmployeeRow(
            employee = EmployeeRowData(
                id = UUID.randomUUID().toString(),
                fullName = "John Doe",
                teamText = "Engineering",
                employeeTypeText = EmployeeType.FULL_TIME.displayString,
                photoUrlSmall = "https://www.example.com/photo.jpg"
            ),
            onEmployeeSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeRowDarkPreview() {
    AppTheme(darkTheme = true) {
        EmployeeRow(
            employee = EmployeeRowData(
                id = UUID.randomUUID().toString(),
                fullName = "John Doe",
                teamText = "Engineering",
                employeeTypeText = EmployeeType.FULL_TIME.displayString,
                photoUrlSmall = "https://www.example.com/photo.jpg"
            ),
            onEmployeeSelected = {},
        )
    }
}

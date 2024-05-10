package work.wander.directory.ui.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import work.wander.directory.ui.common.LoadingIndicatorView
import work.wander.directory.ui.common.PicassoImage

@Composable
fun EmployeeScreenView(
    uiState: EmployeeScreenUiState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            EmployeeScreenTopBar(
                uiState = uiState,
                onBackSelected = onBackSelected,
                onSettingsSelected = onSettingsSelected,
            )
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            EmployeeScreenContents(
                uiState = uiState,
                modifier = modifier
                    .fillMaxSize()
            )
        }
    }

}

@Composable
fun EmployeeScreenContents(uiState: EmployeeScreenUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        is EmployeeScreenUiState.Initial,
        is EmployeeScreenUiState.Loading,
        -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                LoadingIndicatorView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        is EmployeeScreenUiState.Error -> {
            Text("Employee Details Not Available (${uiState.message})", modifier = modifier)
        }

        is EmployeeScreenUiState.Success -> {
            EmployeeDetailsView(employee = uiState.employee, modifier = modifier)
        }
    }
}

@Composable
fun EmployeeDetailsView(employee: EmployeeDetailsData, modifier: Modifier = Modifier,
                        onCallRequested: () -> Unit = {}, onEmailRequested: () -> Unit = {}) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PicassoImage(
                url = employee.photoUrl,
                modifier = Modifier
                    .size(128.dp)

                    .clip(shape = CircleShape),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(employee.fullName)
                Text(employee.teamText)
                Text(employee.employeeTypeText)
            }
        }
        Text(employee.biography)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                onCallRequested()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Call",
                    modifier = Modifier.size(48.dp)
                )

            }
            Text(employee.phoneNumber, modifier = Modifier.weight(1f))
            HorizontalDivider(
                modifier = Modifier
                    .width(4.dp)
            )
            IconButton(onClick = {
                onEmailRequested()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Email",
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(employee.emailAddress, modifier = Modifier.weight(1f))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreenTopBar(
    uiState: EmployeeScreenUiState,
    onBackSelected: () -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {
    val appBarTitle = when (uiState) {
        is EmployeeScreenUiState.Initial,
        is EmployeeScreenUiState.Error -> ""

        is EmployeeScreenUiState.Loading -> "Loading Details..."
        is EmployeeScreenUiState.Success -> uiState.employee.fullName
    }
    TopAppBar(
        title = {
            Text(appBarTitle)
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackSelected()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onSettingsSelected()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Navigate to Settings",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    )
}

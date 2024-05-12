package work.wander.directory.ui.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import work.wander.directory.ui.common.LoadingIndicatorView
import work.wander.directory.ui.common.PicassoImage
import work.wander.directory.ui.theme.AppTheme

@Composable
fun EmployeeScreenView(
    uiState: EmployeeScreenUiState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {},
    onCallRequested: (String) -> Unit = {},
    onEmailRequested: (String) -> Unit = {},
) {

    Scaffold(
        topBar = {
            EmployeeScreenTopBar(
                uiState = uiState,
                onBackSelected = onBackSelected,
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
                    .fillMaxSize(),
                onCallRequested = onCallRequested,
                onEmailRequested = onEmailRequested,
            )
        }
    }

}

@Composable
fun EmployeeScreenContents(
    uiState: EmployeeScreenUiState,
    modifier: Modifier = Modifier,
    onCallRequested: (String) -> Unit = {},
    onEmailRequested: (String) -> Unit = {},
) {
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
            EmployeeDetailsView(
                employee = uiState.employee,
                modifier = modifier,
                onCallRequested = {
                    onCallRequested(uiState.employee.phoneNumber)
                },
                onEmailRequested = {
                    onEmailRequested(uiState.employee.emailAddress)
                })
        }
    }
}

@Composable
fun EmployeeDetailsView(
    employee: EmployeeDetailsData, modifier: Modifier = Modifier,
    onCallRequested: () -> Unit = {}, onEmailRequested: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(start = 32.dp, end = 32.dp)
        ) {
            PicassoImage(
                url = employee.photoUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraSmall),
            )
        }
        Text(
            text = employee.fullName,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = employee.teamText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(${employee.employeeTypeText})",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = employee.biography,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    onCallRequested()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Call",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    EmployeeDetailsData.getFormattedPhoneNumber(employee.phoneNumber),
                    maxLines = 1,
                    modifier = Modifier.padding(
                        end = 4.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    onEmailRequested()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Email",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    employee.emailAddress,
                    maxLines = 1,
                    modifier = Modifier.padding(
                        end = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreenTopBar(
    uiState: EmployeeScreenUiState,
    onBackSelected: () -> Unit = {},
) {
    val appBarTitle = when (uiState) {
        is EmployeeScreenUiState.Initial,
        is EmployeeScreenUiState.Error -> ""

        is EmployeeScreenUiState.Loading -> "Loading Details..."
        is EmployeeScreenUiState.Success -> "Details: " + uiState.employee.fullName
    }
    TopAppBar(
        title = {
            Text(
                appBarTitle,
                style = MaterialTheme.typography.displayLarge,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = {
                onBackSelected()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

@Preview
@Composable
private fun EmployeeScreenSuccessTopBarPreview() {
    AppTheme {
        EmployeeScreenTopBar(
            uiState = EmployeeScreenUiState.Success(
                EmployeeDetailsData(
                    id = "1",
                    fullName = "John Doe",
                    biography = "Biography",
                    teamText = "Team",
                    employeeTypeText = "Employee Type",
                    photoUrl = "https://example.com/photo.jpg",
                    phoneNumber = "123-456-7890",
                    emailAddress = "john_doh@hotmail.com",
                    employeeType = "Contractor",
                )
            )
        )
    }
}

@Preview
@Composable
private fun EmployeeDetailsViewPreview() {
    AppTheme {
        EmployeeDetailsView(
            employee = EmployeeDetailsData(
                id = "1",
                fullName = "John Doe",
                biography = "Biography",
                teamText = "Team",
                employeeTypeText = "Employee Type",
                photoUrl = "https://example.com/photo.jpg",
                phoneNumber = "123-456-7890",
                emailAddress = "john_doh@hotmail.com",
                employeeType = "Contractor",
            )
        )
    }
}

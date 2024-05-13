package work.wander.directory.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import work.wander.directory.data.employee.remote.EmployeeDataUrl
import work.wander.directory.proto.settings.ApplicationSettings
import work.wander.directory.ui.theme.AppTheme

/**
 * Composable function for the Application Settings View.
 *
 * This function creates a view for the application settings. It includes a top bar with a title and a navigation icon.
 * The view contains a column layout with rows for each setting. The settings include a checkbox for enabling developer mode,
 * and additional settings that are available when developer mode is enabled.
 *
 * The function takes an ApplicationSettings object, a modifier for the Scaffold, and two callback functions for when the settings are updated and when the back button is selected.
 *
 * @param applicationSettings The ApplicationSettings object that holds the current settings.
 * @param modifier The Modifier for the Scaffold. This can be used to apply layout and other visual transformations to the Scaffold.
 * @param onSettingsUpdated A callback function that is called when a setting is updated. It takes an updated ApplicationSettings object as a parameter.
 * @param onBackSelected A callback function that is called when the back button in the top bar is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationSettingsView(
    applicationSettings: ApplicationSettings,
    numSavedEmployeeState: State<Int>,
    modifier: Modifier = Modifier,
    onSettingsUpdated: (ApplicationSettings) -> Unit = {},
    onClearLocalDataSelected: () -> Unit = {},
    onBackSelected: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackSelected) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Navigate Back",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    text = "Developer Mode Enabled",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = applicationSettings.isDeveloperModeEnabled,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
                    onCheckedChange = { isChecked ->
                        onSettingsUpdated(
                            applicationSettings.toBuilder().setIsDeveloperModeEnabled(isChecked)
                                .build()
                        )
                    },
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            )
            if (applicationSettings.isDeveloperModeEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Developer Settings",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val currentEmployeeDataUrl =
                                EmployeeDataUrl.fromUrl(applicationSettings.activeEmployeeDataUrl)
                            val isSelectingEmployeeDataUrl = remember { mutableStateOf(false) }
                            Text(
                                text = "Active Directory URL: ",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .weight(0.4f)
                                    .padding(start = 16.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = currentEmployeeDataUrl.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(0.66f)
                                    .padding(end = 8.dp),
                            )
                            IconButton(onClick = { isSelectingEmployeeDataUrl.value = true }) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = "Select Directory URL",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterVertically),
                                )
                            }
                            DropdownMenu(
                                modifier = Modifier.weight(0.60f),
                                expanded = isSelectingEmployeeDataUrl.value,
                                offset = DpOffset(140.dp, 4.dp),
                                onDismissRequest = {
                                    isSelectingEmployeeDataUrl.value = false
                                },
                            ) {
                                EmployeeDataUrl.entries.forEach { employeeDataUrl ->
                                    DropdownMenuItem(
                                        text = { Text(employeeDataUrl.description) },
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .padding(end = 8.dp),
                                        onClick = {
                                            isSelectingEmployeeDataUrl.value = false
                                            onSettingsUpdated(
                                                applicationSettings.toBuilder()
                                                    .setActiveEmployeeDataUrl(employeeDataUrl.url)
                                                    .build()
                                            )
                                        },
                                    )
                                }

                            }

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Number of Saved Employees: ${numSavedEmployeeState.value}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(start = 16.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(top = 4.dp, bottom = 4.dp, end = 8.dp),
                                contentPadding = PaddingValues(4.dp),
                                shape = MaterialTheme.shapes.small,

                                onClick = {
                                    onClearLocalDataSelected()
                                }) {
                                Text(
                                    "Clear Local Database",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ApplicationSettingsViewPreview() {
    val applicationSettings = ApplicationSettings.getDefaultInstance().toBuilder()
        .setIsDeveloperModeEnabled(true)
        .build()

    val numSavedEmployeeState = remember { mutableIntStateOf(0) }
    AppTheme {
        ApplicationSettingsView(
            applicationSettings = applicationSettings,
            numSavedEmployeeState = numSavedEmployeeState,
        )
    }
}

@Preview
@Composable
private fun ApplicationSettingsViewDarkPreview() {
    val applicationSettings = ApplicationSettings.getDefaultInstance().toBuilder()
        .setIsDeveloperModeEnabled(true)
        .build()

    val numSavedEmployeeState = remember { mutableIntStateOf(0) }

    AppTheme(darkTheme = true) {
        ApplicationSettingsView(
            applicationSettings = applicationSettings,
            numSavedEmployeeState = numSavedEmployeeState,
        )
    }
}
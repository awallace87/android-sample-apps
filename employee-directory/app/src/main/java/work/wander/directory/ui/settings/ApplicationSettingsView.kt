package work.wander.directory.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.directory.data.employee.remote.EmployeeDataUrl
import work.wander.directory.proto.settings.ApplicationSettings
import work.wander.directory.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationSettingsView(
    applicationSettings: ApplicationSettings,
    modifier: Modifier = Modifier,
    onSettingsUpdated: (ApplicationSettings) -> Unit = {},
    onBackSelected: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackSelected) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Navigate Back")
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
                            text = "Developer Mode Settings",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Developer Mode is enabled. Additional settings are available.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val currentEmployeeDataUrl = EmployeeDataUrl.fromUrl(applicationSettings.activeEmployeeDataUrl)
                            val isSelectingEmployeeDataUrl = remember { mutableStateOf(false) }
                            Text(
                                text = "Active Directory URL",
                                modifier = Modifier
                                    .weight(0.33f)
                                    .padding(start = 8.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentEmployeeDataUrl.description,
                                modifier = Modifier
                                    .weight(0.66f)
                                    .padding(end = 8.dp)
                                    .clickable {
                                        isSelectingEmployeeDataUrl.value = true
                                    },
                            )
                            DropdownMenu(
                                modifier = Modifier.weight(0.66f),
                                expanded = isSelectingEmployeeDataUrl.value,
                                onDismissRequest = {
                                    isSelectingEmployeeDataUrl.value = false
                                },) {
                                EmployeeDataUrl.entries.forEach { employeeDataUrl ->
                                    DropdownMenuItem(
                                        text = { Text(employeeDataUrl.description) },
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
    AppTheme {
        ApplicationSettingsView(
            applicationSettings = applicationSettings,
        )
    }
}

@Preview
@Composable
private fun ApplicationSettingsViewDarkPreview() {
    val applicationSettings = ApplicationSettings.getDefaultInstance().toBuilder()
        .setIsDeveloperModeEnabled(true)
        .build()
    AppTheme(darkTheme = true) {
        ApplicationSettingsView(
            applicationSettings = applicationSettings,
        )
    }
}
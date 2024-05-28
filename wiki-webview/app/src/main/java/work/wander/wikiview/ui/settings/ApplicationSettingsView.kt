package work.wander.wikiview.ui.settings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.wikiview.proto.settings.ApplicationSettings
import work.wander.wikiview.proto.settings.WikipediaViewSettings
import work.wander.wikiview.ui.theme.AppTheme

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
                        style = MaterialTheme.typography.headlineSmall,
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
                Spacer(modifier = Modifier.height(8.dp))
                WikipediaDisplaySettingsView(
                    wikipediaViewSettings = applicationSettings.wikipediaViewSettings,
                    onWikiDisplaySettingsUpdated = { updatedSettings ->
                        onSettingsUpdated(
                            applicationSettings.toBuilder()
                                .setWikipediaViewSettings(updatedSettings)
                                .build()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun WikipediaDisplaySettingsView(
    wikipediaViewSettings: WikipediaViewSettings,
    modifier: Modifier = Modifier,
    onWikiDisplaySettingsUpdated: (WikipediaViewSettings) -> Unit = {}
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Wikipedia Display Settings",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "HTML Content Type",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier,
            ) {
                Row() {
                    RadioButton(
                        selected = wikipediaViewSettings.selectedHtmlType == WikipediaViewSettings.PageHtmlType.MOBILE,
                        onClick = {
                            onWikiDisplaySettingsUpdated(
                                wikipediaViewSettings.toBuilder()
                                    .setSelectedHtmlType(WikipediaViewSettings.PageHtmlType.MOBILE)
                                    .build()
                            )
                        },
                    )
                    Text(
                        text = "Mobile",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
                Row() {
                    RadioButton(
                        selected = wikipediaViewSettings.selectedHtmlType == WikipediaViewSettings.PageHtmlType.DEFAULT,
                        onClick = {
                            onWikiDisplaySettingsUpdated(
                                wikipediaViewSettings.toBuilder()
                                    .setSelectedHtmlType(WikipediaViewSettings.PageHtmlType.DEFAULT)
                                    .build()
                            )
                        },
                    )
                    Text(
                        text = "Default",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }

            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp),
        )
    }
}

@Preview
@Composable
private fun ApplicationSettingsViewPreview() {
    AppTheme {
        ApplicationSettingsView(
            applicationSettings = ApplicationSettings.getDefaultInstance().toBuilder()
                .setIsDeveloperModeEnabled(true)
                .setWikipediaViewSettings(
                    WikipediaViewSettings.getDefaultInstance().toBuilder()
                        .setSelectedHtmlType(WikipediaViewSettings.PageHtmlType.MOBILE)
                        .build()
                )
                .build(),
        )
    }
}
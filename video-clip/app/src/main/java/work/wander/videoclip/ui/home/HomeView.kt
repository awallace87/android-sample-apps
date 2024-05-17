package work.wander.videoclip.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    onSettingsSelected: () -> Unit = {},
    onRecordingSelected: (String) -> Unit,
    onBeginRecordingSelected: () -> Unit,
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                onSettingsSelected = onSettingsSelected
            )
        },
        content = {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                HomeContent(
                    modifier = Modifier.fillMaxSize(),
                    onRecordingSelected = onRecordingSelected,
                    onBeginRecordingSelected = onBeginRecordingSelected,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(modifier: Modifier = Modifier, onSettingsSelected: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                text = "Video Clips",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier,
        actions = {
            IconButton(onClick = { onSettingsSelected() }) {
                Icon(Icons.Outlined.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onRecordingSelected: (String) -> Unit = {},
    onBeginRecordingSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(text = "Home")
        OutlinedButton(onClick = { onBeginRecordingSelected() }) {
            Text("Begin Recording")
        }
    }
}
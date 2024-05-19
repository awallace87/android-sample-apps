package work.wander.pomodogetter.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.pomodogetter.ui.theme.AppTheme

@Composable
fun HomeView(modifier: Modifier = Modifier, onNewTaskAdded: (String) -> Unit = {}) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar()
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeViewContents(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }
}

@Composable
fun HomeViewContents(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(4.dp))
        HomeViewWelcomeCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        HomeViewAddNewTaskCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        HomeViewTaskListCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun HomeViewWelcomeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        content = {
            Column {
                Text(text = "Welcome to PomodoGetter")
                Text(text = "Get started by adding a new task")
            }
        }
    )
}

@Composable
fun HomeViewAddNewTaskCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        content = {
            Column {
                Text(text = "Add a new task")
            }
        })
}

@Composable
fun HomeViewTaskListCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        content = {
            Column {
                Text(text = "Task List")
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(title = { /*TODO*/ }, modifier = modifier)
}

@Preview
@Composable
private fun HomeViewPreview() {
    AppTheme {
        HomeView()
    }
}

@Preview
@Composable
private fun HomeViewContentsPreview() {
    AppTheme {
        HomeViewContents()
    }
}

@Preview
@Composable
private fun HomeViewWelcomeCardPreview() {
    AppTheme {
        HomeViewWelcomeCard()
    }
}

@Preview
@Composable
private fun HomeViewAddNewTaskCardPreview() {
    AppTheme {
        HomeViewAddNewTaskCard()
    }
}

@Preview
@Composable
private fun HomeViewTaskListCardPreview() {
    AppTheme {
        HomeViewTaskListCard()
    }
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    AppTheme {
        HomeTopAppBar()
    }
}
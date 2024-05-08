package work.wander.directory.ui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import work.wander.directory.data.roomdemo.entity.DemoEntity
import work.wander.directory.ui.theme.ExampleTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDemoView(
    demoDataEntities: List<DemoEntity>,
    modifier: Modifier = Modifier,
    onSettingsSelected: () -> Unit = {},
    onEntityCreate: (String) -> Unit = {},
    onEntityEdit: (Int, String) -> Unit = { _, _ -> },
    onEntityDelete: (Int) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Room Demo",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSettingsSelected()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Go to Settings",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp)
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            RoomDemoDataInput(
                onEntityCreate = onEntityCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(demoDataEntities.size) { exampleDataIndex ->
                    val demoEntity = demoDataEntities[exampleDataIndex]
                    RoomDemoDataRow(
                        demoEntity = demoEntity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        onEdit = { onEntityEdit(demoEntity.id, it)},
                        onDelete = { onEntityDelete(demoEntity.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RoomDemoDataInput(
    onEntityCreate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditingNewEntity = remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        if (isEditingNewEntity.value) {
            val focusRequester = remember { FocusRequester() }
            val userInputText = remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                delay(50)
                focusRequester.requestFocus()
            }
            OutlinedTextField(
                value = userInputText.value,
                onValueChange = { userInputText.value = it },
                label = { Text("Enter Data") },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onEntityCreate(userInputText.value)
                        isEditingNewEntity.value = false
                        userInputText.value = ""
                        keyboardController?.hide()
                    }
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    isEditingNewEntity.value = false
                    userInputText.value = ""
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel adding new item",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                onClick = {
                    isEditingNewEntity.value = true
                }) {
                Text("Add New Data")
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RoomDemoDataRow(
    demoEntity: DemoEntity,
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit = {},
    onDelete: (Int) -> Unit = {},
) {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val localKeyboardController = LocalSoftwareKeyboardController.current
    val isEditing = remember { mutableStateOf(false) }
    val editedData = remember { mutableStateOf(demoEntity.data) }
    Row(
        modifier = modifier,
    ) {
        if (isEditing.value) {
            OutlinedTextField(
                value = editedData.value,
                onValueChange = { editedData.value = it },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        localKeyboardController?.hide()
                        onEdit(editedData.value)
                        isEditing.value = false
                    }
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    localKeyboardController?.hide()
                    editedData.value = demoEntity.data
                    isEditing.value = false
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Edit",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = demoEntity.data,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                val formattedDate = formatter.format(demoEntity.lastModifiedDate.toEpochMilli())
                Text(
                    text = "Last Modified: $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    isEditing.value = true
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Data",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = {
                    onDelete(demoEntity.id)
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Data",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoomDemoViewPreview() {
    ExampleTheme {
        RoomDemoView(
            demoDataEntities = listOf(
                DemoEntity(1, "Example Data 1"),
                DemoEntity(2, "Example Data 2"),
                DemoEntity(3, "Example Data 3"),
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomDemoDataRowPreview() {
    ExampleTheme {
        RoomDemoDataRow(
            demoEntity = DemoEntity(1, "Example Data"),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
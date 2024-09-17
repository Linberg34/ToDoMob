package com.example.todolist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ToDoListPage(toDoList: List<ToDo>, onUpdateToDoList: (List<ToDo>) -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<ToDo?>(null) }
    var idCounter by remember { mutableStateOf(1) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "Add ToDo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(padding)
                .padding(8.dp)
        ) {
            LazyColumn {
                itemsIndexed(toDoList) { index, item ->
                    ToDoItem(
                        item = item,
                        onEditClick = {
                            taskToEdit = item
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            onUpdateToDoList(toDoList.filter { it.id != item.id })
                        },
                        onToggleComplete = { updatedTask ->
                            onUpdateToDoList(toDoList.map {
                                if (it.id == updatedTask.id) updatedTask else it
                            })
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddToDoDialog(
            onAdd = { title, description ->
                onUpdateToDoList(toDoList + ToDo(
                    id = idCounter++,
                    title = title,
                    description = description,
                    isCompleted = false
                ))
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (showEditDialog && taskToEdit != null) {
        EditTask(
            task = taskToEdit!!,
            onEdit = { updatedTask ->
                onUpdateToDoList(toDoList.map {
                    if (it.id == updatedTask.id) updatedTask else it
                })
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}


@Composable
fun ToDoItem(
    item: ToDo,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleComplete: (ToDo) -> Unit
) {
    val isCompleted = item.isCompleted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = if (isCompleted) Color.Green else Color.Gray
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color.White
            )
        }

        IconButton(
            onClick = {
                onToggleComplete(item.copy(isCompleted = !isCompleted))
            },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isCompleted) Color.Green else Color.LightGray)
                .clickable {
                    onToggleComplete(item.copy(isCompleted = !isCompleted))

                }
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Done,
                    contentDescription = "Completed",
                    tint = Color.White
                )
            }
        }

        IconButton(onClick = { onEditClick() }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { onDeleteClick() }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}


@Composable
fun AddToDoDialog(onAdd: (String, String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New ToDo") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        onAdd(title, description)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditTask(task: ToDo, onEdit: (ToDo) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit ToDo") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        onEdit(task.copy(title = title, description = description))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}


package com.example.todolist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToDoListPage() {
    var toDoList by remember { mutableStateOf(listOf<ToDo>()) }
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
            LazyColumn(
                content = {
                    itemsIndexed(toDoList) { index, item ->
                        ToDoItem(
                            item = item,
                            onEditClick = {
                                taskToEdit = item
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                toDoList = toDoList.filter { it.id != item.id }
                            },
                            onToggleComplete = { updatedTask ->
                                toDoList = toDoList.map {
                                    if (it.id == updatedTask.id) updatedTask else it
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    if (showAddDialog) {
        AddToDoDialog(
            onAdd = { title, description ->
                toDoList = toDoList + ToDo(
                    id = idCounter++,
                    title = title,
                    description = description,
                    isCompleted = false
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (showEditDialog && taskToEdit != null) {
        EditTask(
            task = taskToEdit!!,
            onEdit = { updatedTask ->
                toDoList = toDoList.map {
                    if (it.id == updatedTask.id) updatedTask else it
                }
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
    var isCompleted by remember { mutableStateOf(item.isCompleted) }

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
                isCompleted = !isCompleted
                onToggleComplete(item.copy(isCompleted = isCompleted))
            },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isCompleted) Color.Green else Color.LightGray)
        ) {
            if (isCompleted) {
                Icon(painter = painterResource(id = R.drawable.baseline_call_missed_outgoing_24), contentDescription = "Completed", tint = Color.White)
            }
        }

        IconButton(onClick = { onEditClick() }) {
            Icon(painter = painterResource(id = R.drawable.edit_color_24), contentDescription = "Edit")
        }
        IconButton(onClick = { onDeleteClick() }) {
            Icon(painter = painterResource(id = R.drawable.baseline_delete_forever_24), contentDescription = "Delete")
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


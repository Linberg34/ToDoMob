package com.example.todolist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToDoListPage(){

    var toDoList = getFakeToDo()

    Column (
        modifier  = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ){
        LazyColumn(
            content = {
                itemsIndexed(toDoList){ index: Int, item: ToDo ->
                    ToDoItem(item = item)
                }
            }
        )
    }
}

@Composable
fun ToDoItem(item: ToDo){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(15.dp)
    ){
        Column {
            Text(text = item.title, fontSize = 20.sp, color = Color.Gray)
            Text(text = item.description, fontSize = 12.sp, color = Color.White)
        }
    }
}
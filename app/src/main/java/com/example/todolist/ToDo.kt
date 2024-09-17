package com.example.todolist

import kotlinx.serialization.Serializable

@Serializable
data class ToDo(
    var id: Int,
    var title : String,
    var description :String,
    var isCompleted : Boolean
)



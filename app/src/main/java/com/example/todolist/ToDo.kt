package com.example.todolist

data class ToDo(
    var id: Int,
    var title : String,
    var description :String,
    var isCompleted : Boolean
)


fun getFakeToDo() :  List<ToDo>{
    return listOf<ToDo>(
        ToDo(1,"Example","Example",false),
    ToDo(2,"May","Fuck",false)
    );
}
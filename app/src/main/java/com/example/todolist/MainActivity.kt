package com.example.todolist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.todolist.ui.theme.ToDoListTheme
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp()
                }
            }
        }
    }

    @Composable
    fun MyApp() {
        val context = LocalContext.current
        var toDoList by remember { mutableStateOf<List<ToDo>>(emptyList()) }
        var showToast by remember { mutableStateOf<String?>(null) }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val jsonString = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() }
                jsonString?.let {
                    toDoList = Json.decodeFromString(it)
                    showToast = "Data loaded successfully"
                } ?: run {
                    showToast = "Failed to load data"
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
            ) {
                ToDoListPage(toDoList, onUpdateToDoList = { updatedList -> toDoList = updatedList })

                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        val saved = saveToDoList(context, toDoList)
                        showToast = if (saved) "Data saved successfully" else "Failed to save data"
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.LightGray)
                ) {
                    Icon(
                        Icons.Default.SaveAlt, contentDescription = "Save Data", tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        filePickerLauncher.launch("application/json")
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.LightGray)
                ) {
                    Icon(
                        Icons.Default.Upload, contentDescription = "Load Data", tint = Color.Black
                    )
                }
            }

            showToast?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                showToast = null
            }
        }

    }

    fun saveToDoList(context: Context, toDoList: List<ToDo>): Boolean {
        return try {
            val jsonString = Json.encodeToString(toDoList)

            val externalMediaDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ToDolist")

            if (!externalMediaDir.exists()) {
                externalMediaDir.mkdirs()
            }
            val fileName = generateIncrementedFileName(externalMediaDir, "storage")
            val file = File(externalMediaDir, fileName)

            FileOutputStream(file).use { output ->
                output.write(jsonString.toByteArray())
            }
            Log.d("FileOperations", "File saved successfully at ${file.absolutePath}")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun generateIncrementedFileName(directory: File, baseName: String): String {
        var index = 1
        var fileName = "$baseName$index.json"

        while (File(directory, fileName).exists()) {
            index++
            fileName = "$baseName$index.json"
        }

        return fileName
    }

}

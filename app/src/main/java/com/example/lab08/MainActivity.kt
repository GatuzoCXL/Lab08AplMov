package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch
import com.example.lab08.ui.theme.Lab08Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                )

                //migraciones
                .addMigrations(TaskDatabase.MIGRATION_1_2)
                .build()


                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)


                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(1) }
    var newTaskCategory by remember { mutableStateOf("") }
    var editTaskDescription by remember { mutableStateOf("") }
    var editTaskPriority by remember { mutableStateOf(1) }
    var editTaskCategory by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = newTaskPriority.toString(),
            onValueChange = { if (it.isNotEmpty()) newTaskPriority = it.toInt() },
            label = { Text("Prioridad (1-3)") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = newTaskCategory,
            onValueChange = { newTaskCategory = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription, newTaskPriority, newTaskCategory)
                    newTaskDescription = ""
                    newTaskPriority = 1
                    newTaskCategory = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }


        Spacer(modifier = Modifier.height(16.dp))


        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar tareas") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (searchQuery.isNotEmpty()) {
                    viewModel.searchTasks(searchQuery)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Buscar")
        }


        tasks.forEach { task ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (editingTask == task) {
                    Column(modifier = Modifier.weight(1f)) {
                        TextField(
                            value = editTaskDescription,
                            onValueChange = { editTaskDescription = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = editTaskPriority.toString(),
                            onValueChange = { if (it.isNotEmpty()) editTaskPriority = it.toInt() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = editTaskCategory,
                            onValueChange = { editTaskCategory = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Button(onClick = {
                        viewModel.updateTask(
                            task.copy(
                                description = editTaskDescription,
                                priority = editTaskPriority,
                                category = editTaskCategory
                            )
                        )
                        editingTask = null
                    }) {
                        Text("Guardar")
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = task.description)
                        Text(text = "Prioridad: ${task.priority}")
                        Text(text = "Categoría: ${task.category}")
                    }
                    Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                        Text(if (task.isCompleted) "Completada" else "Pendiente")
                    }
                    Button(onClick = {
                        editTaskDescription = task.description
                        editTaskPriority = task.priority
                        editTaskCategory = task.category
                        editingTask = task
                    }) {
                        Text("Editar")
                    }
                    Button(onClick = {
                        coroutineScope.launch {
                            viewModel.updateTask(task.copy(isCompleted = true))
                        }
                    }) {
                        Text("Eliminar")
                    }
                }
            }
        }


        Button(
            onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}
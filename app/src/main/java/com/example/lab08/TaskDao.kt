package com.example.lab08

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface TaskDao {


    // Obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>


    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)


    // Marcar una tarea como completada o no completada
    @Update
    suspend fun updateTask(task: Task)


    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    // Filtrar tareas completadas
    @Query("SELECT * FROM tasks WHERE is_completed = 1")
    suspend fun getCompletedTasks(): List<Task>

    // Filtrar tareas pendientes
    @Query("SELECT * FROM tasks WHERE is_completed = 0")
    suspend fun getPendingTasks(): List<Task>

    // Ordenar tareas por prioridad
    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    suspend fun getTasksByPriority(): List<Task>

    // Ordenar tareas por fecha de creación
    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    suspend fun getTasksByCreationDate(): List<Task>

    // Buscar tareas por descripción
    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :search || '%'")
    suspend fun searchTasks(search: String): List<Task>
}
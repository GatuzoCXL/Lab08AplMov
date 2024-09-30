package com.example.lab08

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration //nuevas importaciones
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Task::class], version = 2) //modificacion a veriosn 2
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

//migraciones
    companion object{
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL DEFAULT 1, `category` TEXT NOT NULL DEFAULT '')")
            }
        }
    }
}
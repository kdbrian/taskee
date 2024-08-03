package io.junrdev.github.taskee.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.junrdev.github.taskee.data.dao.TaskDao
import io.junrdev.github.taskee.model.Converters
import io.junrdev.github.taskee.model.Task

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskeeDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var db: TaskeeDatabase? = null

        private val MIG1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    
                    ALTER TABLE Task ADD COLUMN
                    
                """.trimIndent())
                db.beginTransaction()
            }
        }

        fun getRoomDb(context: Context): TaskeeDatabase {
            return db ?: synchronized(this) {
                buildDatabase(context).also { db = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, TaskeeDatabase::class.java, "taskeedb")
                .addMigrations(MIG1_2)
                .build()
    }
}
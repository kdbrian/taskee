package io.junrdev.github.taskee.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.junrdev.github.taskee.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM TASK")
    suspend fun getTasks(): List<Task>

    @Insert
    suspend fun addTask(task: Task)

    @Query("SELECT * FROM TASK WHERE id= :id")
    suspend fun getTaskById(id : Long) : Task

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM TASK WHERE isDone=1")
    suspend fun getNotDoneTasks(): List<Task>
}
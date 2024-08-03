package io.junrdev.github.taskee.data

import io.junrdev.github.taskee.data.dao.TaskDao
import io.junrdev.github.taskee.model.Task

class TaskRepository(
    val appDb: TaskeeDatabase
) {

    private var taskDao: TaskDao = appDb.taskDao()

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun getAllTasks(): List<Task> {
        return taskDao.getTasks()
    }

    suspend fun getDoneTasks(): List<Task> {
        return taskDao.getNotDoneTasks()
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(id: Long): Task {
        return taskDao.getTaskById(id)
    }
}
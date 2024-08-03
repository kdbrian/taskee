package io.junrdev.github.taskee.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.junrdev.github.taskee.data.TaskeeDatabase
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskViewModel(
    val context: Context
) : ViewModel() {

    private val appDB = TaskeeDatabase.getRoomDb(context)
    private val taskDao = appDB.taskDao()

    private val _tasks = MutableLiveData<MutableList<Task>>()
    val tasks: LiveData<MutableList<Task>> = _tasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            try {
                val taskList = taskDao.getTasks().toMutableList()
                _tasks.postValue(taskList)
            } catch (e: Exception) {
                Timber.e(e, "Error fetching tasks")
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                taskDao.addTask(task)
                val updatedList = taskDao.getTasks().toMutableList()
                _tasks.postValue(updatedList)
            } catch (e: Exception) {
                Timber.e(e, "Error adding task")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskDao.updateTask(task)
                val updatedList = taskDao.getTasks().toMutableList()
                _tasks.postValue(updatedList)
            } catch (e: Exception) {
                Timber.e(e, "Error updating task")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskDao.deleteTask(task)
                val updatedList = taskDao.getTasks().toMutableList()
                _tasks.postValue(updatedList)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting task")
            }
        }
    }
}

class MyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

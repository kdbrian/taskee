package io.junrdev.github.taskee.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.junrdev.github.taskee.data.TaskeeDatabase
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskViewModel (
    val context: Context
) : ViewModel() {

    private val appDB = TaskeeDatabase.getRoomDb(context)

    private val taskDao = appDB.taskDao()

    private val _tasks = MutableLiveData<List<Task>>()

    val tasks: LiveData<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            _tasks.value = taskDao.getTasks()
        }
    }

    suspend fun getTasks() = viewModelScope.async { taskDao.getTasks() }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.addTask(task)
            _tasks.value = taskDao.getTasks()
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
            _tasks.value = taskDao.getTasks()
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            _tasks.value = taskDao.getTasks()
        }
    }
}

class MyViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return super.create(modelClass)
    }
}
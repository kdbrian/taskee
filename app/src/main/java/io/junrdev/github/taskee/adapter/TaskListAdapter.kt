package io.junrdev.github.taskee.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.junrdev.github.taskee.R
import io.junrdev.github.taskee.adapter.TaskListAdapter.VH
import io.junrdev.github.taskee.data.TaskRepository
import io.junrdev.github.taskee.data.dao.TaskDao
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListAdapter(
    val taskRepository: TaskRepository,
    private var taskList: MutableList<Task> = mutableListOf(),
    val onitemclicked: ((task: Task) -> Unit)
) : RecyclerView.Adapter<VH>() {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            taskList = scope.async {
                taskRepository.getAllTasks().sortedBy { it.isDone }.toMutableList()
            }.await()

            Log.d(TAG, "tasklist: $taskList")
        }
    }


    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val taskttile: TextView = view.findViewById(R.id.tasktitle)
        val taskitem: LinearLayout = view.findViewById(R.id.taskitem)

        fun bind(task: Task) {
            taskttile.text = task.title
            taskitem.setOnClickListener {
                onitemclicked.invoke(task)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.taskitempreview, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(taskList[position])
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = taskList.removeAt(fromPosition)
        taskList.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun toggleIsDone(position: Int) {
        val task = taskList.get(position)
        task.isDone = !task.isDone
        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.updateTask(task)
            withContext(Dispatchers.Main) {
                val task = taskList.removeAt(position)

                if (task.isDone) {
                    taskList.add(task)
                    notifyItemMoved(position, taskList.size - 1)
                    notifyItemChanged(taskList.size - 1)
                } else {
                    taskList.add(0, task)
                    notifyItemMoved(position, 0)
                    notifyItemChanged(0)
                }
            }
        }
    }

    companion object {
        private const val TAG = "TaskListAdapter"
    }
}

package io.junrdev.github.taskee.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.junrdev.github.taskee.R
import io.junrdev.github.taskee.model.Task

class TaskAdapter(
    val tasks: List<Task>,
    onDelete: ((task: Task) -> Unit)? = null,
    onUpdate: ((task: Task) -> Unit)? = null,
    onSelected: ((task: Task) -> Unit)? = null
) : RecyclerView.Adapter<TaskAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.tasktitle)
        fun bindItem(task: Task) {
            title.text = task.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.taskitempreview, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindItem(tasks[position])
    }
}
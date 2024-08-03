package io.junrdev.github.taskee.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.junrdev.github.taskee.R
import io.junrdev.github.taskee.data.TaskRepository
import io.junrdev.github.taskee.data.TaskeeDatabase
import io.junrdev.github.taskee.databinding.ViewsingletaskBinding
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_ITEM_ID = "taskid"

class ViewTask : Fragment(R.layout.viewsingletask) {

    lateinit var taskRepository: TaskRepository
    lateinit var binding: ViewsingletaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewsingletaskBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskRepository = TaskRepository(TaskeeDatabase.getRoomDb(requireContext()))

        arguments?.getParcelable<Task>("task")?.let { tsk ->
            binding.apply {
                task = tsk

                deletetask.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        taskRepository.updateTask(tsk.copy(isDone = true))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Task Deleted", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().popBackStack()
                        }
                    }
                }

                imageView.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_viewTask_to_editTask,
                        bundleOf("task" to tsk)
                    )
                }

                description.apply {
                    setFontSize(6)
                    setPadding(10, 10, 10, 10)
                    setEditorFontColor(R.color.priorityNormal)
                    html = tsk.description
                    isEnabled = false
                    setBackgroundColor(resources.getColor(android.R.color.transparent))
                }
            }


        }

    }
}
package io.junrdev.github.taskee.screens

import android.R.drawable.ic_input_add
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import io.junrdev.github.taskee.R
import io.junrdev.github.taskee.adapter.TaskListAdapter
import io.junrdev.github.taskee.data.TaskRepository
import io.junrdev.github.taskee.data.TaskeeDatabase
import io.junrdev.github.taskee.databinding.ViewtasksBinding
import io.junrdev.github.taskee.model.Task

class ViewTasks : Fragment(R.layout.viewtasks) {

    lateinit var taskRepository: TaskRepository
    lateinit var taskListAdapter: TaskListAdapter
    lateinit var binding: ViewtasksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewtasksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskRepository = TaskRepository(appDb = TaskeeDatabase.getRoomDb(requireContext()))
        taskListAdapter = TaskListAdapter(taskRepository = taskRepository) {
            findNavController().navigate(R.id.action_viewTasks_to_viewTask, bundleOf("task" to it))
        }

        binding.apply {
            tasklist.apply {
                adapter = taskListAdapter
                layoutManager = LinearLayoutManager(
                    requireContext().applicationContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }

            addtaskitem.setOnClickListener {
                findNavController().navigate(R.id.action_viewTasks_to_addTask)
            }

        }


        val itemtouchhelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                taskListAdapter.moveItem(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                taskListAdapter.toggleIsDone(viewHolder.adapterPosition)
            }
        }

        val touchHelper = ItemTouchHelper(itemtouchhelper)
        touchHelper.attachToRecyclerView(binding.tasklist)

        requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE).apply {
            if (!getBoolean(firstrun, false)) {
                TapTargetSequence(requireActivity())
                    .targets(
                        TapTarget.forView(view.findViewById(R.id.title), "This is just a title")
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(10)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.black)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .targetRadius(60),
                        TapTarget.forView(
                            binding.addtaskitem,
                            "Use this to add tasks"
                        )
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(10)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.black)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .icon(requireContext().getDrawable(ic_input_add))
                            .targetRadius(60),

                        TapTarget.forView(
                            view.findViewById(R.id.tasklist),
                            "Your tasks will appear here.",
                            "Ordered from not attended to attended to tasks"
                        )
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(10)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.black)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .targetRadius(60)
                    ).listener(
                        object : TapTargetSequence.Listener {
                            override fun onSequenceFinish() {

                                edit().apply {
                                    putBoolean(firstrun, true)
                                }.apply()

                                Toast.makeText(
                                    requireContext(),
                                    "Welcome to thee app.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onSequenceStep(
                                lastTarget: TapTarget?,
                                targetClicked: Boolean
                            ) {
                            }

                            override fun onSequenceCanceled(lastTarget: TapTarget?) {
                                TODO("Not yet implemented")
                            }
                        }
                    ).start()
            }
        }
    }


    companion object {
        private const val firstrun = "firstrun"
    }
}
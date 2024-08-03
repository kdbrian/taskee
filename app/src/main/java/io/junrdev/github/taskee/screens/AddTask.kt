package io.junrdev.github.taskee.screens

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.junrdev.github.taskee.R
import io.junrdev.github.taskee.data.TaskRepository
import io.junrdev.github.taskee.data.TaskeeDatabase
import io.junrdev.github.taskee.databinding.AddtaskitemBinding
import io.junrdev.github.taskee.model.Priority
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTask : Fragment(R.layout.addtaskitem) {

    lateinit var taskRepository: TaskRepository
    var desc: String? = null
    lateinit var binding: AddtaskitemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddtaskitemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskRepository = TaskRepository(appDb = TaskeeDatabase.getRoomDb(requireContext()))

        binding.apply {
            editor.setFontSize(6)
            editor.setPadding(10, 10, 10, 10)
            editor.setEditorFontColor(R.color.priorityNormal)
            editor.setPlaceholder("Write your task here...\n <li>You can use a list</li>\n<li><b>Bold Text</b></li>\n<li><i>Italic Text</i></li>")
            editor.setEditorHeight(300)
            editor.setBackgroundColor(resources.getColor(android.R.color.transparent))

            var isBold = false
            var isItalic = false
            var isList = false


            boldtext.setOnClickListener {
                editor.setBold()
            }

            italictext.setOnClickListener {
                editor.setItalic()
            }


            listtext.setOnClickListener {
                editor.setBullets()
            }

            pickPriority.setOnClickListener {
                context?.let { ctx ->
                    val dialog = MaterialAlertDialogBuilder(ctx).apply {
                        setView(R.layout.selectprioritydialog)
                        setOnDismissListener(DialogInterface::dismiss)
                        setCancelable(true)
                    }.create()

                    dialog.let { dlg ->
                        print("first here")
                        dlg.findViewById<LinearLayout>(R.id.normal)?.setOnClickListener {
                            dialog.dismiss()
                            print("then here")
                            pickPriority.setBackgroundColor(resources.getColor(R.color.priorityNormal))
                        }
                        dlg.findViewById<LinearLayout>(R.id.medium)?.setOnClickListener {
                            dialog.dismiss()
                            pickPriority.setBackgroundColor(resources.getColor(R.color.priorityMedium))
                        }
                        dlg.findViewById<LinearLayout>(R.id.high)?.setOnClickListener {
                            dialog.dismiss()
                            pickPriority.setBackgroundColor(resources.getColor(R.color.priorityHigh))
                        }
                        dialog.show()
                    }

                }
            }

            savetask.setOnClickListener {
                if (editTextText.text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        taskRepository.addTask(
                            Task(
                                title = editTextText.text.toString(),
                                description = desc,
                                priority = Priority.Normal
                            )
                        )

                        withContext(Dispatchers.Main) {
                            showToast("Task added successfully.")
                            findNavController().popBackStack()
                        }

                    }
                }
            }

        }


        requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .apply {
                if (!getBoolean(addtaskfirstrun, false)) {
                    TapTargetSequence(requireActivity())
                        .targets(
                            TapTarget.forView(
                                binding.editTextText,
                                "Title",
                                "Enter your task title"
                            ),
                            TapTarget.forView(
                                binding.editor,
                                "Description",
                                "Enter your task description, completely optional"
                            ),
                            TapTarget.forView(
                                binding.savetask,
                                "Save task",
                                "Submit your task for saving by click here"
                            )
                        ).listener(
                            object : TapTargetSequence.Listener {
                                override fun onSequenceFinish() {
                                    edit().putBoolean(addtaskfirstrun, true).apply()
                                }

                                override fun onSequenceStep(
                                    lastTarget: TapTarget?,
                                    targetClicked: Boolean
                                ) {
                                }

                                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                                }

                            }
                        )
                        .start()
                }
            }


        binding.editor.setOnTextChangeListener {
            desc = it
        }


    }

    private fun showToast(s: String) {
        context?.let {
            Toast.makeText(it, s, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "AddTask"
        const val addtaskfirstrun = "addtaskfirstrun"
    }
}

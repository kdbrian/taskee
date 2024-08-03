package io.junrdev.github.taskee.screens

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Html
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
import io.junrdev.github.taskee.databinding.EdittaskitemBinding
import io.junrdev.github.taskee.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class EditTask : Fragment(R.layout.edittaskitem) {

    private lateinit var tsk: Task
    private lateinit var task: Task
    var desc: String? = null
    lateinit var binding: EdittaskitemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EdittaskitemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editor.setFontSize(6)
            editor.setPadding(10, 10, 10, 10)
            editor.setEditorFontColor(Color.BLACK)
            editor.setEditorHeight(300)

                boldtext.setOnClickListener {
                    editor.setBold()
                }

                italictext.setOnClickListener {
                    editor.setItalic()
                }


                listtext.setOnClickListener {
                    editor.setBullets()
                }
        }

        arguments?.getParcelable("task", Task::class.java)?.let {
            tsk = it
            Timber.d(it.toString())
            binding.apply {
                task = it
                editTextText2.setText(it.description)
                tsk.description?.let { dsc ->
                    editor.setPlaceholder(Html.fromHtml(dsc).toString())
                } ?: editor.setPlaceholder("Add some text")
            }
        }

        requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE).apply {
            if (getBoolean(edittaskfirstrun, false)) {
                TapTargetSequence(requireActivity())
                    .targets(
                        TapTarget.forView(
                            view.findViewById(R.id.title),
                            "Title",
                            "Enter your task title"
                        ),
                        TapTarget.forView(
                            view.findViewById(R.id.boldtext),
                            "Bold",
                            "Tap on this once to type bold, and again to disable bold text."
                        ),
                        TapTarget.forView(
                            view.findViewById(R.id.italictext),
                            "Italic",
                            "Use this to enable italic text"
                        ),
                        TapTarget.forView(
                            view.findViewById(R.id.listtext),
                            "List",
                            "Carefully detail your task in a list"
                        ),
                        TapTarget.forView(
                            view.findViewById(R.id.editor),
                            "Description",
                            "Enter your task description, completely optional"
                        ),
                    ).listener(
                        object : TapTargetSequence.Listener {
                            override fun onSequenceFinish() {
                                edit().putBoolean(edittaskfirstrun, true).apply()
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

        binding.apply {

            updatetask.setOnClickListener {
                tsk.let {
                    if (editTextText2.text!!.isNotEmpty()) {
                        val tt = editTextText2.text.toString()

                        it.title = tt
                        it.description = desc

                        CoroutineScope(Dispatchers.IO).launch {
                            val taskRepository =
                                TaskRepository(appDb = TaskeeDatabase.getRoomDb(requireContext()))
                            taskRepository.updateTask(it)

                            withContext(Dispatchers.Main) {
                                findNavController().popBackStack(R.id.viewTasks, false)
                            }
                        }

                    } else
                        Toast.makeText(
                            requireContext(),
                            "Task must have a title",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                }
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


            editor.setOnTextChangeListener {
                desc = it
            }

        }

    }

    companion object {
        const val edittaskfirstrun = "edittaskfirstrun"
    }
}

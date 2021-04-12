package com.example.planner.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.NotificationsActivity
import com.example.planner.R
import com.example.planner.SearchableActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment(), AddTaskDialogFragment.AddTaskDialogListener,
    AddListDialogFragment.AddListDialogListener {

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_tasks, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.taskRecyclerView)
        taskAdapter = TaskAdapter(mutableListOf())
        val addTaskButton: FloatingActionButton = view.findViewById(R.id.addTaskButton)

        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskAdapter.fetchTasks(requireContext())

        addTaskButton.setOnClickListener {
            val dialog = AddTaskDialogFragment()
            dialog.show(childFragmentManager, "addTask")
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_tasks_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(requireContext(), SearchableActivity::class.java))
                true
            }
            R.id.notifications -> {
                startActivity(Intent(requireContext(), NotificationsActivity::class.java))
                true
            }
            R.id.hideTasks -> {
                item.isChecked = !item.isChecked
                true
            }
            else -> false
        }
    }

    override fun onAddTaskDialogPositiveClick(dialog: DialogFragment, task: Task) {
        task.id = taskAdapter.getLastId()
        taskAdapter.addTask(task)
        taskAdapter.saveTasks(requireContext())
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onAddListDialogPositiveClick(dialog: DialogFragment) {
        // TODO
    }

}
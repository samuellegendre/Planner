package com.example.planner.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.planner.NotificationsActivity
import com.example.planner.R
import com.example.planner.SearchableActivity
import com.example.planner.ui.calendar.AddClassDialogFragment
import com.example.planner.ui.calendar.Event

class TasksFragment : Fragment(), AddTaskDialogFragment.AddTaskDialogListener, AddListDialogFragment.AddListDialogListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_tasks, container, false)
        val addTaskButton: Button = view.findViewById(R.id.addTaskButton)
        val addListButton: Button = view.findViewById(R.id.addListButton)
        val listActions: Button = view.findViewById(R.id.listActions)
        val taskActions: Button = view.findViewById(R.id.taskActions)

        addTaskButton.setOnClickListener {
            val dialog = AddTaskDialogFragment()
            dialog.show(childFragmentManager, "addTask")
        }
        addListButton.setOnClickListener {
            val dialog = AddListDialogFragment()
            dialog.show(childFragmentManager, "addList")
        }
        listActions.setOnClickListener {
            val popup = PopupMenu(requireContext(), listActions)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.fragment_tasks_list_dropdown, popup.menu)
            popup.show()
        }
        taskActions.setOnClickListener {
            val popup = PopupMenu(requireContext(), taskActions)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.fragment_tasks_task_dropdown, popup.menu)
            popup.show()
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

    override fun onAddTaskDialogPositiveClick(dialog: DialogFragment) {
        // TODO
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onAddListDialogPositiveClick(dialog: DialogFragment) {
        // TODO
    }

}
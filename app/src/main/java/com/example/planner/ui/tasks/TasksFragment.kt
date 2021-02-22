package com.example.planner.ui.tasks

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.planner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_tasks, container, false)
        val floatingActionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext()).setTitle("Ajouter").show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks, menu)
    }

}
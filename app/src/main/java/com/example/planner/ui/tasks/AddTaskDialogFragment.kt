package com.example.planner.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.ui.calendar.DatePickerFragment
import com.example.planner.ui.calendar.TimePickerFragment

class AddTaskDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_task, null)

            val dateButton: Button = view.findViewById(R.id.dateButton)
            val timeButton: Button = view.findViewById(R.id.timeButton)
            val spinner: Spinner = view.findViewById(R.id.tags)

            dateButton.setOnClickListener {
                DatePickerFragment().show(childFragmentManager, "datePicker")
            }

            timeButton.setOnClickListener {
                TimePickerFragment().show(childFragmentManager, "timePicker")
            }

            ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.tags,
                    android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            builder.setView(view)
                    .setTitle(R.string.add_task)
                    .setPositiveButton(R.string.add,
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                            })
            builder.create()
        } ?: throw IllegalStateException()
    }
}
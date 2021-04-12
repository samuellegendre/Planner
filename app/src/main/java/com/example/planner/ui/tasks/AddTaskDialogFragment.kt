package com.example.planner.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.ui.dialogs.DatePickerFragment
import com.example.planner.ui.dialogs.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class AddTaskDialogFragment : DialogFragment(), DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {
    private lateinit var listener: AddTaskDialogListener
    private var calendar: Calendar = Calendar.getInstance()
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    interface AddTaskDialogListener {
        fun onAddTaskDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = parentFragment as AddTaskDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement AddTaskDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_task, null)

            val dateButton: Button = view.findViewById(R.id.dateButton)
            val timeButton: Button = view.findViewById(R.id.timeButton)
            val spinner: Spinner = view.findViewById(R.id.tags)

            dateButton.setOnClickListener {
                DatePickerFragment(dateButton, calendar).show(childFragmentManager, "datePicker")
            }

            timeButton.setOnClickListener {
                TimePickerFragment(timeButton, calendar).show(childFragmentManager, "timePicker")
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
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.onAddTaskDialogPositiveClick(this)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException()
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        button: Button,
        calendar: Calendar
    ) {
        calendar.set(year, month, dayOfMonth)
        button.text = dateFormat.format(calendar.time)
    }

    override fun onTimeSet(
        view: TimePicker?,
        hourOfDay: Int,
        minute: Int,
        button: Button,
        calendar: Calendar
    ) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        button.text = timeFormat.format(calendar.time)
    }
}
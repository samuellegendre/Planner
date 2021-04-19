package com.example.planner.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.ui.dialogs.DatePickerFragment
import com.example.planner.ui.dialogs.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class ModifyTaskDialogFragment(private val task: Task) : DialogFragment(), DatePickerFragment.DatePickerListener,
    TimePickerFragment.TimePickerListener {
    private lateinit var listener: ModifyTaskDialogListener
    private var calendar: Calendar = task.calendar
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var timeSwitch: SwitchCompat
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    interface ModifyTaskDialogListener {
        fun onModifyTaskDialogPositiveClick(dialog: DialogFragment, task: Task)
        fun onModifyTaskDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = parentFragment as ModifyTaskDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement AddTaskDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_task, null)

            val taskTitle: EditText = view.findViewById(R.id.taskName)
            val taskDescription: EditText = view.findViewById(R.id.taskDescription)
            val dateSwitch: SwitchCompat = view.findViewById(R.id.addDateSwitch)
            dateButton = view.findViewById(R.id.dateButton)
            timeSwitch = view.findViewById(R.id.addTimeSwitch)
            timeButton = view.findViewById(R.id.timeButton)

            taskTitle.setText(task.title)
            taskDescription.setText(task.description)
            dateSwitch.isChecked = task.hasDate
            timeSwitch.isChecked = task.hasTime

            dateSwitch.setOnCheckedChangeListener { _, isChecked ->
                disableButtons(isChecked)
            }

            timeSwitch.setOnCheckedChangeListener { _, isChecked ->
                timeButton.isEnabled = isChecked
            }

            dateButton.text = dateFormat.format(calendar.time)
            timeButton.text = timeFormat.format(calendar.time)

            dateButton.setOnClickListener {
                DatePickerFragment(dateButton, calendar).show(childFragmentManager, "datePicker")
            }

            timeButton.setOnClickListener {
                TimePickerFragment(timeButton, calendar).show(childFragmentManager, "timePicker")
            }

            builder.setView(view)
                .setTitle(R.string.add_task)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.onModifyTaskDialogPositiveClick(
                        this,
                        Task(
                            if (taskTitle.text.toString()
                                    .isBlank()
                            ) "Sans titre" else taskTitle.text.toString(),
                            taskDescription.text.toString(),
                            calendar,
                            dateSwitch.isChecked,
                            timeSwitch.isChecked
                        )
                    )
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onModifyTaskDialogNegativeClick(this)
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

    private fun disableButtons(isChecked: Boolean) {
        dateButton.isEnabled = isChecked
        timeSwitch.isEnabled = isChecked
        if (!isChecked) timeSwitch.isChecked = isChecked
    }
}
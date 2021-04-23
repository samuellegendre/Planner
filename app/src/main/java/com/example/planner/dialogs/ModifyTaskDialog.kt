package com.example.planner.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.Task
import java.text.SimpleDateFormat
import java.util.*

class ModifyTaskDialog(private val task: Task) : DialogFragment(),
    DatePickDialog.DatePickerListener,
    TimePickDialog.TimePickerListener {
    private lateinit var listener: ModifyTaskDialogListener
    private lateinit var dialog: AlertDialog
    private var calendar: Calendar = task.calendar
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var timeSwitch: SwitchCompat
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    interface ModifyTaskDialogListener {
        fun onModifyTaskDialogPositiveClick(dialog: DialogFragment, task: Task)
        fun onModifyTaskDialogNegativeClick(dialog: DialogFragment, task: Task)
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
            taskTitle.requestFocus()
            taskDescription.setText(task.description)
            dateSwitch.isChecked = task.hasDate
            timeSwitch.isChecked = task.hasTime
            dateSwitchDisableButtons(dateSwitch.isChecked)
            timeSwitchDisableButton(timeSwitch.isChecked)

            dateSwitch.setOnCheckedChangeListener { _, isChecked ->
                dateSwitchDisableButtons(isChecked)
            }

            timeSwitch.setOnCheckedChangeListener { _, isChecked ->
                timeSwitchDisableButton(isChecked)
            }

            dateButton.text = dateFormat.format(calendar.time)
            timeButton.text = timeFormat.format(calendar.time)

            dateButton.setOnClickListener {
                DatePickDialog(dateButton, calendar).show(childFragmentManager, "datePicker")
            }

            timeButton.setOnClickListener {
                TimePickDialog(timeButton, calendar).show(childFragmentManager, "timePicker")
            }

            builder.setView(view)
                .setTitle(R.string.modify_task)
                .setPositiveButton(
                    R.string.save
                ) { _, _ ->
                    listener.onModifyTaskDialogPositiveClick(
                        this,
                        Task(
                            task.id,
                            if (taskTitle.text.toString()
                                    .isBlank()
                            ) "Sans titre" else taskTitle.text.toString(),
                            taskDescription.text.toString(),
                            calendar,
                            dateSwitch.isChecked,
                            timeSwitch.isChecked,
                            task.isChecked
                        )
                    )
                }
                .setNeutralButton(R.string.cancel) { _, _ -> this.dismiss() }
                .setNegativeButton(
                    R.string.delete
                ) { _, _ ->
                    listener.onModifyTaskDialogNegativeClick(this, task)
                }
            dialog = builder.create()
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog
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

    private fun dateSwitchDisableButtons(isChecked: Boolean) {
        dateButton.isEnabled = isChecked
        timeSwitch.isEnabled = isChecked
        if (!isChecked) timeSwitch.isChecked = isChecked
    }

    private fun timeSwitchDisableButton(isChecked: Boolean) {
        timeButton.isEnabled = isChecked
    }
}
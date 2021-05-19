package com.example.planner.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.Task
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class ModifyTaskDialog(private val task: Task) : DialogFragment(),
    DatePickDialog.DatePickerListener,
    TimePickDialog.TimePickerListener {
    private lateinit var listener: ModifyTaskDialogListener
    private lateinit var dialog: AlertDialog
    private var calendar: Calendar = task.calendar
    private lateinit var dateButton: TextView
    private lateinit var timeButton: TextView
    private lateinit var timeSwitch: SwitchCompat
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var toolbar: Toolbar

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
            val builder = AlertDialog.Builder(it, R.style.Theme_Planner_FullScreenDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_task, null)

            toolbar = view.findViewById(R.id.toolbar)
            val taskTitle: TextInputLayout = view.findViewById(R.id.taskName)
            val taskDescription: TextInputLayout = view.findViewById(R.id.taskDescription)
            val dateSwitch: SwitchCompat = view.findViewById(R.id.addDateSwitch)
            dateButton = view.findViewById(R.id.dateButton)
            timeSwitch = view.findViewById(R.id.addTimeSwitch)
            timeButton = view.findViewById(R.id.timeButton)

            toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }
            toolbar.setTitle(R.string.modify_task)
            toolbar.inflateMenu(R.menu.dialog_modify_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save -> {
                        listener.onModifyTaskDialogPositiveClick(
                            this,
                            Task(
                                task.id,
                                if (taskTitle.editText?.text.toString()
                                        .isBlank()
                                ) "Sans titre" else taskTitle.editText?.text.toString(),
                                taskDescription.editText?.text.toString(),
                                calendar,
                                dateSwitch.isChecked,
                                timeSwitch.isChecked,
                                task.isChecked
                            )
                        )
                        dismiss()
                        true
                    }
                    R.id.delete -> {
                        listener.onModifyTaskDialogNegativeClick(this, task)
                        dismiss()
                        true
                    }
                    else -> {
                        dismiss()
                        true
                    }
                }
            }

            taskTitle.editText?.setText(task.title)
            taskDescription.editText?.setText(task.description)
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
            dialog = builder.create()
            dialog
        } ?: throw IllegalStateException()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setWindowAnimations(R.style.Theme_Planner_FullScreenDialog_Animations)
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        button: TextView,
        calendar: Calendar
    ) {
        calendar.set(year, month, dayOfMonth)
        button.text = dateFormat.format(calendar.time)
    }

    override fun onTimeSet(
        view: TimePicker?,
        hourOfDay: Int,
        minute: Int,
        button: TextView,
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
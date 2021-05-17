package com.example.planner.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.Task
import java.text.SimpleDateFormat
import java.util.*

class AddTaskDialog : DialogFragment(), DatePickDialog.DatePickerListener,
    TimePickDialog.TimePickerListener {
    private lateinit var listener: AddTaskDialogListener
    private lateinit var dialog: AlertDialog
    private var calendar: Calendar = Calendar.getInstance()
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var toolbar: Toolbar

    interface AddTaskDialogListener {
        fun onAddTaskDialogPositiveClick(dialog: DialogFragment, task: Task)
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
            val builder = AlertDialog.Builder(it, R.style.Theme_Planner_FullScreenDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_task, null)

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            toolbar = view.findViewById(R.id.toolbar)
            val taskTitle: EditText = view.findViewById(R.id.taskName)
            val taskDescription: EditText = view.findViewById(R.id.taskDescription)
            val dateSwitch: SwitchCompat = view.findViewById(R.id.addDateSwitch)
            val dateButton: Button = view.findViewById(R.id.dateButton)
            val timeSwitch: SwitchCompat = view.findViewById(R.id.addTimeSwitch)
            val timeButton: Button = view.findViewById(R.id.timeButton)

            toolbar.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                dialog.dismiss()
            }
            toolbar.setTitle(R.string.add_task)
            toolbar.inflateMenu(R.menu.dialog_add_menu)
            toolbar.setOnMenuItemClickListener {
                listener.onAddTaskDialogPositiveClick(
                    this,
                    Task(
                        0,
                        if (taskTitle.text.toString()
                                .isBlank()
                        ) "Sans titre" else taskTitle.text.toString(),
                        taskDescription.text.toString(),
                        calendar,
                        dateSwitch.isChecked,
                        timeSwitch.isChecked
                    )
                )
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                dismiss()
                true
            }

            taskTitle.requestFocus()

            dateSwitch.setOnCheckedChangeListener { _, isChecked ->
                dateButton.isEnabled = isChecked
                timeSwitch.isEnabled = isChecked
                if (!isChecked) timeSwitch.isChecked = isChecked
            }

            timeSwitch.setOnCheckedChangeListener { _, isChecked ->
                timeButton.isEnabled = isChecked
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
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
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
package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.example.planner.R
import com.example.planner.utils.DatePickerFragment
import com.example.planner.utils.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class AddClassDialogFragment : DialogFragment(), DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {

    private lateinit var listener: AddClassDialogListener
    private lateinit var startDateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endDateButton: Button
    private lateinit var endTimeButton: Button
    private var startCalendar = Calendar.getInstance()
    private var endCalendar = Calendar.getInstance()
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    interface AddClassDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, startCalendar: Calendar, endCalendar: Calendar)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as AddClassDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_class, null)
            val builder = AlertDialog.Builder(it)

            val className: EditText = view.findViewById(R.id.className)
            startDateButton = view.findViewById(R.id.startDateButton)
            startTimeButton = view.findViewById(R.id.startTimeButton)
            endDateButton = view.findViewById(R.id.endDateButton)
            endTimeButton = view.findViewById(R.id.endTimeButton)
            val classLocation: EditText = view.findViewById(R.id.classLocation)
            val spinner: Spinner = view.findViewById(R.id.classMethod)

            startDateButton.text = dateFormat.format(startCalendar.time)
            startTimeButton.text = timeFormat.format(startCalendar.time)
            endDateButton.text = dateFormat.format(endCalendar.time)
            endTimeButton.text = timeFormat.format(endCalendar.time)

            startDateButton.setOnClickListener {
                val datePickerFragment = DatePickerFragment(startDateButton, startCalendar)
                datePickerFragment.show(childFragmentManager, "startDatePicker")
            }

            startTimeButton.setOnClickListener {
                val timePickerFragment = TimePickerFragment(startTimeButton, startCalendar)
                timePickerFragment.show(childFragmentManager, "startTimePicker")
            }

            endDateButton.setOnClickListener {
                val datePickerFragment = DatePickerFragment(endDateButton, endCalendar)
                datePickerFragment.show(childFragmentManager, "endDatePicker")
            }

            endTimeButton.setOnClickListener {
                val timePickerFragment = TimePickerFragment(endTimeButton, endCalendar)
                timePickerFragment.show(childFragmentManager, "endTimePicker")
            }

            // Spinner
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.teaching_methods,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            builder.setView(view)
                .setTitle(R.string.addClass)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.onDialogPositiveClick(this, startCalendar, endCalendar)
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
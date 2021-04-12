package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.DatePickerFragment
import com.example.planner.utils.TimePickerFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class AddClassDialogFragment : DialogFragment(), DatePickerFragment.DatePickerListener,
    TimePickerFragment.TimePickerListener {

    private lateinit var listener: AddClassDialogListener
    private lateinit var dialog: AlertDialog
    private lateinit var startDateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endDateButton: Button
    private lateinit var endTimeButton: Button
    private var startCalendar = Calendar.getInstance()
    private var endCalendar = Calendar.getInstance()
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var validated = true

    interface AddClassDialogListener {
        fun onAddClassDialogPositiveClick(dialog: DialogFragment, event: Event)
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
            val allDaySwitch: SwitchMaterial = view.findViewById(R.id.allDaySwitch)
            startDateButton = view.findViewById(R.id.startDateButton)
            startTimeButton = view.findViewById(R.id.startTimeButton)
            endDateButton = view.findViewById(R.id.endDateButton)
            endTimeButton = view.findViewById(R.id.endTimeButton)
            val classLocation: EditText = view.findViewById(R.id.classLocation)
            val spinner: Spinner = view.findViewById(R.id.classMethod)

            allDaySwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startTimeButton.isEnabled = false
                    endTimeButton.isEnabled = false
                } else {
                    startTimeButton.isEnabled = true
                    endTimeButton.isEnabled = true
                }
            }

            when (startCalendar.get(Calendar.MINUTE)) {
                0 -> startCalendar.set(Calendar.MINUTE, 0)
                in 1..15 -> startCalendar.set(Calendar.MINUTE, 15)
                in 16..30 -> startCalendar.set(Calendar.MINUTE, 30)
                in 31..45 -> startCalendar.set(Calendar.MINUTE, 45)
                in 46..59 -> {
                    startCalendar.set(Calendar.MINUTE, 0)
                    startCalendar.set(Calendar.HOUR, startCalendar.get(Calendar.HOUR) + 1)
                }
            }
            endCalendar.time = startCalendar.time
            endCalendar.set(Calendar.HOUR, startCalendar.get(Calendar.HOUR) + 1)

            startDateButton.text = dateFormat.format(startCalendar.time)
            startTimeButton.text = timeFormat.format(startCalendar.time)
            endDateButton.text = dateFormat.format(endCalendar.time)
            endTimeButton.text = timeFormat.format(endCalendar.time)

            spinner.adapter = TeachingMethodArrayAdapter(requireContext(), TeachingMethods.list!!)

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

            builder.setView(view)
                .setTitle(R.string.addClass)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    if (allDaySwitch.isChecked && startCalendar.time == endCalendar.time) {
                        endCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE) + 1)
                    }
                    listener.onAddClassDialogPositiveClick(
                        this, Event(
                            0,
                            if (className.text.toString()
                                    .isBlank()
                            ) "Sans titre" else className.text.toString(),
                            classLocation.text.toString(),
                            startCalendar,
                            endCalendar,
                            (spinner.selectedItem as TeachingMethod).color,
                            allDaySwitch.isChecked
                        )
                    )
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            dialog = builder.create()
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

        if (button == startDateButton && endCalendar < startCalendar) {
            endCalendar.set(year, month, dayOfMonth)
            endDateButton.text = dateFormat.format(calendar.time)
        }

        calendarValidation()
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

        calendarValidation()
    }

    private fun calendarValidation() {
        validated = if (endCalendar < startCalendar) {
            startDateButton.setTextColor(Color.RED)
            startTimeButton.setTextColor(Color.RED)
            false
        } else {
            startDateButton.setTextColor(Color.BLACK)
            startTimeButton.setTextColor(Color.BLACK)
            true
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = validated
    }
}
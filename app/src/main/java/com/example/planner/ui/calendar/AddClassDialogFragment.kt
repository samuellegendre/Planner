package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.DatePickerFragment
import com.example.planner.utils.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class AddClassDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var listener: AddClassDialogListener
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private var calendar = Calendar.getInstance()

    interface AddClassDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, calendar: Calendar)
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
            dateButton = view.findViewById(R.id.dateButton)
            timeButton = view.findViewById(R.id.timeButton)
            val classLocation: EditText = view.findViewById(R.id.classLocation)
            val spinner: Spinner = view.findViewById(R.id.classMethod)

            val datePickerFragment = DatePickerFragment()
            dateButton.setOnClickListener {
                datePickerFragment.show(childFragmentManager, "datePicker")
            }

            val timePickerFragment = TimePickerFragment()
            timeButton.setOnClickListener {
                timePickerFragment.show(childFragmentManager, "timePicker")
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
                    listener.onDialogPositiveClick(this, calendar)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.set(year, month, dayOfMonth)
        dateButton.text = dateFormat.format(calendar.time)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        timeButton.text = timeFormat.format(calendar.time)
    }
}
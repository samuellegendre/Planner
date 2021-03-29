package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.utils.DatePickerFragment
import com.example.planner.utils.TimePickerFragment

class AddClassDialogFragment : DialogFragment(), DatePickerFragment.DatePickerListener {

    private lateinit var listener: AddClassDialogListener

    interface AddClassDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
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
            val dateButton: Button = view.findViewById(R.id.dateButton)
            val timeButton: Button = view.findViewById(R.id.timeButton)
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
                .setPositiveButton(R.string.add
                ) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }
}
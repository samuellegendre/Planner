package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.planner.R

class AddClassDialogFragment : DialogFragment() {

    internal lateinit var listener: AddClassDialogListener

    interface AddClassDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AddClassDialogListener
        } catch (e: ClassCastException) {
            //throw ClassCastException((context.toString() + " must implement NoticeDialogListener"))
        }
    }

    /*private var title: String = ""
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var hour: Int = 0
    private var location: String = ""
    // TODO: Type
    private var method: String = ""*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_class, null)
            val builder = AlertDialog.Builder(it)

            val dateButton: Button = view.findViewById(R.id.dateButton)
            val timeButton: Button = view.findViewById(R.id.timeButton)
            val spinner: Spinner = view.findViewById(R.id.classMethod)
            /*val classLocation: EditText = view.findViewById(R.id.classLocation)
            val className: EditText = view.findViewById(R.id.className)*/

            val datePickerFragment = DatePickerFragment()
            dateButton.setOnClickListener {
                datePickerFragment.show(childFragmentManager, "datePicker")
                /*year = datePickerFragment.getYear()
                month = datePickerFragment.getMonth()
                day = datePickerFragment.getDay()*/
            }

            val timePickerFragment = TimePickerFragment()
            timeButton.setOnClickListener {
                timePickerFragment.show(childFragmentManager, "timePicker")
            }

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
                    .setPositiveButton(R.string.add,
                            DialogInterface.OnClickListener { dialog, id ->
                                listener.onDialogPositiveClick(this)
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                listener.onDialogNegativeClick(this)
                            })
            builder.create()
        } ?: throw IllegalStateException()

    }

    /*fun getTitle(): String {
        return title
    }

    fun getDatePickerYear(): Int {
        return year
    }

    fun getDatePickerMonth(): Int {
        return month
    }

    fun getDatePickerDay(): Int {
        return day
    }

    fun getHour(): Int {
        return hour
    }

    fun getLocation(): String {
        return location
    }

    fun getMethod(): String {
        return method
    }*/

}
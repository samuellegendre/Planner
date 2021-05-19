package com.example.planner.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickDialog(private val button: TextView, private val calendar: Calendar) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var listener: DatePickerListener

    interface DatePickerListener {
        fun onDateSet(
            view: DatePicker?,
            year: Int,
            month: Int,
            dayOfMonth: Int,
            button: TextView,
            calendar: Calendar
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as DatePickerListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement OnDateSetListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireActivity(), this,
            year,
            month,
            dayOfMonth
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener.onDateSet(view, year, month, dayOfMonth, button, calendar)
    }

    override fun onCancel(dialog: DialogInterface) {
        dialog.dismiss()
    }
}
package com.example.planner.ui.calendar

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.util.*
import kotlin.properties.Delegates

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var setYear: Int = 0
    private var setMonth: Int = 0
    private var setDay: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        setYear = year
        setMonth = month
        setDay = dayOfMonth
    }

    fun getYear(): Int {
        return setYear
    }

    fun getMonth(): Int {
        return setMonth
    }

    fun getDay(): Int {
        return setDay
    }

}
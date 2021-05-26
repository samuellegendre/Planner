package com.example.planner.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.example.planner.adapters.Event
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class AddClassDialog(time: Calendar) : DialogFragment(), DatePickDialog.DatePickerListener,
    TimePickDialog.TimePickerListener {

    private lateinit var listener: AddClassDialogListener
    private lateinit var dialog: AlertDialog
    private lateinit var startDateButton: TextView
    private lateinit var startTimeButton: TextView
    private lateinit var endDateButton: TextView
    private lateinit var endTimeButton: TextView
    private var startCalendar = time
    private var endCalendar = Calendar.getInstance()
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var validated = true
    private lateinit var defaultButtonColors: ColorStateList
    private lateinit var toolbar: Toolbar

    interface AddClassDialogListener {
        fun onAddClassDialogPositiveClick(dialog: DialogFragment, event: Event)
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
            val view = inflater.inflate(R.layout.dialog_class, null)
            val builder = AlertDialog.Builder(it, R.style.Theme_Planner_FullScreenDialog)

            toolbar = view.findViewById(R.id.toolbar)
            val className: TextInputLayout = view.findViewById(R.id.className)
            val allDaySwitch: SwitchMaterial = view.findViewById(R.id.allDaySwitch)
            startDateButton = view.findViewById(R.id.startDateButton)
            startTimeButton = view.findViewById(R.id.startTimeButton)
            endDateButton = view.findViewById(R.id.endDateButton)
            endTimeButton = view.findViewById(R.id.endTimeButton)
            val classLocation: TextInputLayout = view.findViewById(R.id.classLocation)
            val textInputLayout: TextInputLayout = view.findViewById(R.id.color)
            val autoCompleteTextView = textInputLayout.editText as? AutoCompleteTextView
            val palette: ImageView = view.findViewById(R.id.palette)

            toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }
            toolbar.setTitle(R.string.add_class)
            toolbar.inflateMenu(R.menu.dialog_add_menu)
            toolbar.setOnMenuItemClickListener {
                if (allDaySwitch.isChecked && startCalendar.time == endCalendar.time) {
                    endCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE) + 1)
                }
                listener.onAddClassDialogPositiveClick(
                    this, Event(
                        0,
                        if (className.editText?.text.toString()
                                .isBlank()
                        ) "Sans titre" else className.editText?.text.toString(),
                        classLocation.editText?.text.toString(),
                        startCalendar,
                        endCalendar,
                        convertStringToColor(autoCompleteTextView?.text.toString()),
                        allDaySwitch.isChecked
                    )
                )
                dismiss()
                true
            }

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

            defaultButtonColors = startDateButton.textColors

            autoCompleteTextView?.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    resources.getStringArray(R.array.colors)
                )
            )
            autoCompleteTextView?.setText("Rouge", false)
            palette.setColorFilter(convertStringToColor("Rouge"))
            autoCompleteTextView?.setOnItemClickListener { parent, _, position, _ ->
                palette.setColorFilter(
                    convertStringToColor(
                        parent.adapter.getItem(position).toString()
                    )
                )
            }

            startDateButton.setOnClickListener {
                val datePickerFragment = DatePickDialog(startDateButton, startCalendar)
                datePickerFragment.show(childFragmentManager, "startDatePicker")
            }

            startTimeButton.setOnClickListener {
                val timePickerFragment = TimePickDialog(startTimeButton, startCalendar)
                timePickerFragment.show(childFragmentManager, "startTimePicker")
            }

            endDateButton.setOnClickListener {
                val datePickerFragment = DatePickDialog(endDateButton, endCalendar)
                datePickerFragment.show(childFragmentManager, "endDatePicker")
            }

            endTimeButton.setOnClickListener {
                val timePickerFragment = TimePickDialog(endTimeButton, endCalendar)
                timePickerFragment.show(childFragmentManager, "endTimePicker")
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
        button: TextView,
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
            startDateButton.setTextColor(defaultButtonColors)
            startTimeButton.setTextColor(defaultButtonColors)
            true
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = validated
    }

    private fun convertStringToColor(color: String): Int {
        when (color) {
            "Rouge" -> return resources.getColor(R.color.red)
            "Orange" -> return resources.getColor(R.color.orange)
            "Jaune" -> return resources.getColor(R.color.yellow)
            "Vert" -> return resources.getColor(R.color.green)
            "Bleu" -> return resources.getColor(R.color.blue)
            "Violet" -> return resources.getColor(R.color.purple)
            "Gris" -> return resources.getColor(R.color.gray)
        }
        return resources.getColor(R.color.gray)
    }
}
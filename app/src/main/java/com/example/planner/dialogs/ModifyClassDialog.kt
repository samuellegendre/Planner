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

class ModifyClassDialog(private val event: Event) : DialogFragment(),
    DatePickDialog.DatePickerListener,
    TimePickDialog.TimePickerListener {

    private lateinit var listener: ModifyClassDialogListener
    private lateinit var dialog: AlertDialog
    private lateinit var startDateButton: TextView
    private lateinit var startTimeButton: TextView
    private lateinit var endDateButton: TextView
    private lateinit var endTimeButton: TextView
    private var startCalendar = event.startTime
    private var endCalendar = event.endTime
    private var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private var validated = true
    private lateinit var defaultButtonColors: ColorStateList
    private lateinit var toolbar: Toolbar

    interface ModifyClassDialogListener {
        fun onModifyClassDialogPositiveClick(dialog: DialogFragment, event: Event)
        fun onModifyClassDialogNegativeClick(dialog: DialogFragment, event: Event)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as ModifyClassDialogListener
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
            val spinner: TextInputLayout = view.findViewById(R.id.classMethod)
            val autoCompleteTextView = spinner.editText as? AutoCompleteTextView
            val palette: ImageView = view.findViewById(R.id.paletteIcon)

            toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }
            toolbar.setTitle(R.string.modify_class)
            toolbar.inflateMenu(R.menu.dialog_modify_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save -> {
                        if (allDaySwitch.isChecked && startCalendar.time == endCalendar.time) {
                            endCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE) + 1)
                        }
                        listener.onModifyClassDialogPositiveClick(
                            this, Event(
                                event.id,
                                if (className.editText?.text.toString()
                                        .isBlank()
                                ) "Sans titre" else className.editText?.text.toString(),
                                classLocation.editText?.text.toString(),
                                startCalendar,
                                endCalendar,
                                convertStringtoColor(autoCompleteTextView?.text.toString()),
                                allDaySwitch.isChecked
                            )
                        )
                        dismiss()
                        true
                    }
                    R.id.delete -> {
                        listener.onModifyClassDialogNegativeClick(this, event)
                        dismiss()
                        true
                    }
                    else -> {
                        dismiss()
                        true
                    }
                }
            }

            className.editText?.setText(event.title)
            allDaySwitch.isChecked = event.isAllDay
            disableButtons(allDaySwitch.isChecked)
            allDaySwitch.setOnCheckedChangeListener { _, isChecked ->
                disableButtons(isChecked)
            }

            startDateButton.text = dateFormat.format(startCalendar.time)
            startTimeButton.text = timeFormat.format(startCalendar.time)
            endDateButton.text = dateFormat.format(endCalendar.time)
            endTimeButton.text = timeFormat.format(endCalendar.time)

            defaultButtonColors = startDateButton.textColors


            classLocation.editText?.setText(event.subtitle)

            autoCompleteTextView?.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    resources.getStringArray(R.array.colors)
                )
            )
            autoCompleteTextView?.setText(convertColorToString(event.color), false)
            palette.setColorFilter(event.color)
            autoCompleteTextView?.setOnItemClickListener { parent, _, position, _ ->
                palette.setColorFilter(
                    convertStringtoColor(
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

    private fun disableButtons(isChecked: Boolean) {
        if (isChecked) {
            startTimeButton.isEnabled = false
            endTimeButton.isEnabled = false
        } else {
            startTimeButton.isEnabled = true
            endTimeButton.isEnabled = true
        }
    }

    private fun convertStringtoColor(color: String): Int {
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

    private fun convertColorToString(color: Int): String {
        when (color) {
            resources.getColor(R.color.red) -> return "Rouge"
            resources.getColor(R.color.orange) -> return "Orange"
            resources.getColor(R.color.yellow) -> return "Jaune"
            resources.getColor(R.color.green) -> return "Vert"
            resources.getColor(R.color.blue) -> return "Bleu"
            resources.getColor(R.color.purple) -> return "Violet"
            resources.getColor(R.color.gray) -> return "Gris"
        }
        return "Rouge"
    }
}
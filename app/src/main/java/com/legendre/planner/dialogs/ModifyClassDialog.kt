package com.legendre.planner.dialogs

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
import com.legendre.planner.R
import com.legendre.planner.adapters.Event
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
            throw ClassCastException()
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
            toolbar.setTitle(R.string.modify_class)
            toolbar.inflateMenu(R.menu.dialog_modify_menu)
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.save -> {
                        if (endCalendar.timeInMillis - startCalendar.timeInMillis < 960000) {
                            endCalendar.set(
                                Calendar.MINUTE,
                                startCalendar.get(Calendar.MINUTE) + 16
                            )
                        }
                        listener.onModifyClassDialogPositiveClick(
                            this, Event(
                                event.id,
                                if (className.editText?.text.toString()
                                        .isBlank()
                                ) resources.getString(R.string.no_title) else className.editText?.text.toString(),
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

        toolbar.menu.getItem(0).isEnabled = validated
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

    private fun convertStringToColor(color: String): Int {
        when (color) {
            resources.getStringArray(R.array.colors)[0] -> return resources.getColor(R.color.red)
            resources.getStringArray(R.array.colors)[1] -> return resources.getColor(R.color.orange)
            resources.getStringArray(R.array.colors)[2] -> return resources.getColor(R.color.yellow)
            resources.getStringArray(R.array.colors)[3] -> return resources.getColor(R.color.green)
            resources.getStringArray(R.array.colors)[4] -> return resources.getColor(R.color.blue)
            resources.getStringArray(R.array.colors)[5] -> return resources.getColor(R.color.purple)
            resources.getStringArray(R.array.colors)[6] -> return resources.getColor(R.color.gray)
        }
        return resources.getColor(R.color.gray)
    }

    private fun convertColorToString(color: Int): String {
        when (color) {
            resources.getColor(R.color.red) -> return resources.getStringArray(R.array.colors)[0]
            resources.getColor(R.color.orange) -> return resources.getStringArray(R.array.colors)[1]
            resources.getColor(R.color.yellow) -> return resources.getStringArray(R.array.colors)[2]
            resources.getColor(R.color.green) -> return resources.getStringArray(R.array.colors)[3]
            resources.getColor(R.color.blue) -> return resources.getStringArray(R.array.colors)[4]
            resources.getColor(R.color.purple) -> return resources.getStringArray(R.array.colors)[5]
            resources.getColor(R.color.gray) -> return resources.getStringArray(R.array.colors)[6]
        }
        return resources.getStringArray(R.array.colors)[0]
    }
}
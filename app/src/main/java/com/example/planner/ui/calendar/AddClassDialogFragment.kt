package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.planner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ClassCastException
import java.text.DateFormat
import java.util.*

class AddClassDialogFragment : DialogFragment() {

    /*private lateinit var listener: AddClassDialogListener

    interface AddClassDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AddClassDialogListener
            val listener: AddClassDialogListener = context
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() + "must implement NoticeDialogListener"))
        }
    }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_addclass, null))
                .setTitle("Ajouter un cours")
                .setPositiveButton("Sauvegarder",
                    DialogInterface.OnClickListener { dialog, id ->
                        //listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton("Annuler",
                    DialogInterface.OnClickListener { dialog, id ->
                        //listener.onDialogNegativeClick(this)
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}
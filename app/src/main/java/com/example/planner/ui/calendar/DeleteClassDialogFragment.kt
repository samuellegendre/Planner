package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.planner.R

class DeleteClassDialogFragment(private val event: Event) : DialogFragment() {

    private lateinit var listener: DeleteClassDialogListener

    interface DeleteClassDialogListener {
        fun onDeleteClassDialogPositiveClick(dialog: DialogFragment, event: Event)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = parentFragment as DeleteClassDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement DeleteClassDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.delete_class)
                .setPositiveButton(
                    R.string.delete
                ) { _, _ ->
                    listener.onDeleteClassDialogPositiveClick(this, event)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }

}
package com.example.planner.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.planner.R

class AddListDialogFragment : DialogFragment() {
    private lateinit var listener: AddListDialogListener

    interface AddListDialogListener {
        fun onAddListDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = parentFragment as AddListDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement AddListDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_add_list, null))
                .setTitle(R.string.add_list)
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.onAddListDialogPositiveClick(this)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException()
    }
}
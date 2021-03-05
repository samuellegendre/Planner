package com.example.planner.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.planner.R

class AddListDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_add_list, null))
                    .setTitle(R.string.add_list)
                    .setPositiveButton(R.string.add,
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                            })
            builder.create()
        } ?: throw IllegalStateException()
    }
}
package com.example.planner.ui.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.planner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CalendarFragment : Fragment()/*,
    AddClassDialogFragment.AddClassDialogListener*/ {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        val floatingActionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener{
            val dialog = AddClassDialogFragment()
            dialog.show(childFragmentManager, "addClass")

            /*val timeButton: Button = dialog.requireView().findViewById(R.id.timeButton)

            timeButton.setOnClickListener {
                TimePickerDialog().show(childFragmentManager, "timePicker")
            }*/
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Recherche").show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun onDialogPositiveClick(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }*/

}
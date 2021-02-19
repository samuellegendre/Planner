package com.example.planner.ui.calendar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.planner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_calendar, container, false)
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
            R.id.action_today -> {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Aujourd'hui").show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
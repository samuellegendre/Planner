package com.example.planner.ui.calendar

import android.os.Bundle
import android.view.*
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.planner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.view.OnSwipeTouchListener
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var collapsibleCalendar : CollapsibleCalendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        //collapsibleCalendar = view.findViewById(R.id.collapsibleCalendar)
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
            R.id.action_today -> {
                //collapsibleCalendar.changeToToday()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}
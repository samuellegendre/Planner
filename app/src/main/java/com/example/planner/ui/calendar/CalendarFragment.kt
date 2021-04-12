package com.example.planner.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alamkanak.weekview.WeekView
import com.example.planner.R
import com.example.planner.SearchableActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(),
    AddClassDialogFragment.AddClassDialogListener,
    DeleteClassDialogFragment.DeleteClassDialogListener {

    private val viewModel by viewModels<CalendarViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        val addClassButton: FloatingActionButton = view.findViewById(R.id.addClassButton)

        val weekView: WeekView = view.findViewById(R.id.weekView)
        val adapter = CalendarSimpleAdapter(this)

        viewModel.fetchEvents(requireContext())

        weekView.adapter = adapter

        weekView.setTimeFormatter {
            "$it h 00"
        }

        weekView.setDateFormatter {
            SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            ).format(it.time) + "\n" + SimpleDateFormat(
                "MM-dd",
                Locale.getDefault()
            ).format(it.time)
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events.entities)
        }

        addClassButton.setOnClickListener {
            val dialog = AddClassDialogFragment()
            dialog.show(childFragmentManager, "addClass")
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_calendar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(requireContext(), SearchableActivity::class.java))
                true
            }
            R.id.today -> {
                weekView.scrollToDateTime(Calendar.getInstance())
                true
            }
            R.id.viewByDay, R.id.viewBy3Day, R.id.viewByWeek -> {
                item.isChecked = !item.isChecked
                when (item.itemId) {
                    R.id.viewByDay -> weekView.numberOfVisibleDays = 1
                    R.id.viewBy3Day -> weekView.numberOfVisibleDays = 3
                    R.id.viewByWeek -> weekView.numberOfVisibleDays = 7
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAddClassDialogPositiveClick(dialog: DialogFragment, event: Event) {
        event.id = viewModel.getLastId()
        viewModel.addEvent(event)
        viewModel.saveEvents(requireContext())
    }

    override fun onDeleteClassDialogPositiveClick(dialog: DialogFragment, event: Event) {
        viewModel.removeEvent(event)
        viewModel.saveEvents(requireContext())
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

}
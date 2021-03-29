package com.example.planner.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import com.example.planner.R
import com.example.planner.SearchableActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class CalendarFragment : Fragment(),
    AddClassDialogFragment.AddClassDialogListener {

    private val viewModel by viewModels<CalendarViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        val addClassButton: FloatingActionButton = view.findViewById(R.id.addClassButton)
        // <WeekViewEntity.Event>
        val adapter = PagingAdapter()
        val weekView: WeekView = view.findViewById(R.id.weekView)

        weekView.adapter = adapter
        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }


        addClassButton.setOnClickListener {
            val dialog = AddClassDialogFragment()
            dialog.onAttach(requireContext())
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
            R.id.viewByDay, R.id.viewBy3Day, R.id.viewByWeek -> {
                item.isChecked = !item.isChecked
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Snackbar.make(requireView(), "Test", Snackbar.LENGTH_SHORT)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}
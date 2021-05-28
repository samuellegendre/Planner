package com.legendre.planner.fragments

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alamkanak.weekview.WeekView
import com.legendre.planner.R
import com.legendre.planner.adapters.CalendarSimpleAdapter
import com.legendre.planner.adapters.Event
import com.legendre.planner.dialogs.AddClassDialog
import com.legendre.planner.dialogs.ModifyClassDialog
import com.legendre.planner.models.CalendarViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(),
    AddClassDialog.AddClassDialogListener,
    ModifyClassDialog.ModifyClassDialogListener {

    private val viewModel by viewModels<CalendarViewModel>()
    private lateinit var addClassButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        addClassButton = view.findViewById(R.id.addClassButton)

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
            ).format(it.time)[0].toUpperCase() + "\n" + SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(it.time)
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events.entities)
        }

        addClassButton.setOnClickListener {
            val dialog = AddClassDialog(Calendar.getInstance())
            dialog.show(childFragmentManager, "addClass")
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_calendar_menu, menu)
    }

    override fun onStart() {
        super.onStart()

        val appBarLayout: AppBarLayout = requireActivity().findViewById(R.id.appBarLayout)
        val toolbar: MaterialToolbar = requireActivity().findViewById(R.id.toolbar)

        appBarLayout.stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.appbar_elevation_off)

        toolbar.title =
            SimpleDateFormat("MMM", Locale.getDefault()).format(Calendar.getInstance().time)
                .capitalize(Locale.ROOT)
    }

    override fun onResume() {
        super.onResume()

        addClassButton.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    override fun onPause() {
        super.onPause()

        val appBarLayout: AppBarLayout = requireActivity().findViewById(R.id.appBarLayout)

        addClassButton.hide()
        appBarLayout.stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.appbar_elevation_on)
    }

    override fun onAddClassDialogPositiveClick(dialog: DialogFragment, event: Event) {
        event.id = viewModel.getLastId()
        viewModel.addEvent(event)
        viewModel.saveEvents(requireContext())
    }

    override fun onModifyClassDialogPositiveClick(dialog: DialogFragment, event: Event) {
        viewModel.updateEvent(event)
        viewModel.saveEvents(requireContext())
    }

    override fun onModifyClassDialogNegativeClick(dialog: DialogFragment, event: Event) {
        viewModel.removeEvent(event)
        viewModel.saveEvents(requireContext())
    }
}
package com.legendre.planner.adapters

import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import com.legendre.planner.R
import com.legendre.planner.dialogs.AddClassDialog
import com.legendre.planner.dialogs.ModifyClassDialog
import com.legendre.planner.fragments.CalendarFragment
import com.legendre.planner.utils.CalendarSerializer
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Event(
    var id: Long,
    var title: String,
    var subtitle: String,
    @Serializable(with = CalendarSerializer::class)
    var startTime: Calendar,
    @Serializable(with = CalendarSerializer::class)
    var endTime: Calendar,
    var color: Int,
    var isAllDay: Boolean
)

class CalendarSimpleAdapter(private val calendarFragment: CalendarFragment) :
    WeekView.SimpleAdapter<Event>() {

    override fun onCreateEntity(item: Event): WeekViewEntity {
        val style = WeekViewEntity.Style.Builder()
            .setBackgroundColor(item.color)
            .build()

        return WeekViewEntity.Event.Builder(item)
            .setId(item.id)
            .setTitle(item.title)
            .setSubtitle(item.subtitle)
            .setStartTime(item.startTime)
            .setEndTime(item.endTime)
            .setStyle(style)
            .setAllDay(item.isAllDay)
            .build()
    }

    override fun onEventClick(data: Event) {
        val dialog = ModifyClassDialog(data)
        dialog.show(calendarFragment.childFragmentManager, "modifyClassDialog")
    }

    override fun onEmptyViewClick(time: Calendar) {
        val dialog = AddClassDialog(time)
        dialog.show(calendarFragment.childFragmentManager, "addClassDialog")
    }

    override fun onRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar) {
        val toolbar: MaterialToolbar = calendarFragment.requireActivity().findViewById(R.id.toolbar)
        val time =
            if (firstVisibleDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                SimpleDateFormat("MMM", Locale.getDefault()).format(firstVisibleDate.time)
                    .capitalize(Locale.ROOT)
            } else {
                SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(firstVisibleDate.time)
                    .capitalize(Locale.ROOT)
            }

        toolbar.title = time
    }
}
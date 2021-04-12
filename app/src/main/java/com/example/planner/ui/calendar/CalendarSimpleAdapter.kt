package com.example.planner.ui.calendar

import androidx.core.content.ContextCompat
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import com.example.planner.utils.CalendarSerializer
import kotlinx.serialization.Serializable
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
            .setBackgroundColor(ContextCompat.getColor(context, item.color))
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
        val dialog = DeleteClassDialogFragment(data)
        dialog.show(calendarFragment.childFragmentManager, "test")
    }

    override fun onEventLongClick(data: Event) {
        // TODO
    }

    override fun onEmptyViewClick(time: Calendar) {
        // TODO
    }

    override fun onEmptyViewLongClick(time: Calendar) {
        // TODO
    }

    override fun onRangeChanged(firstVisibleDate: Calendar, lastVisibleDate: Calendar) {
        // TODO
    }

}
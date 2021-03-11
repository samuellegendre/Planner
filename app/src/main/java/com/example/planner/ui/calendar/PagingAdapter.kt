package com.example.planner.ui.calendar

import android.graphics.RectF
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import java.util.*

data class MyEvent(
    val id: Long,
    val title: String,
    val startTime: Calendar,
    val endTime: Calendar
)

class PagingAdapter : WeekView.SimpleAdapter<MyEvent>() {

    override fun onCreateEntity(item: MyEvent): WeekViewEntity {
        return WeekViewEntity.Event.Builder(item)
            .setId(item.id)
            .setTitle(item.title)
            .setStartTime(item.startTime)
            .setEndTime(item.endTime)
            .build()
    }

    override fun onEventClick(data: MyEvent) {
        // TODO
    }

    override fun onEventClick(data: MyEvent, bounds: RectF) {
        // TODO
    }

    override fun onEventLongClick(data: MyEvent) {
        // TODO
    }

    override fun onEventLongClick(data: MyEvent, bounds: RectF) {
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
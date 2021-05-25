package com.example.planner.models

import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.R
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.ISwipeable
import java.text.SimpleDateFormat
import java.util.*

class TaskItem : AbstractItem<TaskItem.ViewHolder>(), IDraggable, ISwipeable {

    var id: Long? = null
    var title: String? = null
    var description: String? = null
    var dateTime: Calendar? = null
    var hasDate: Boolean? = null
    var hasTime: Boolean? = null
    var color: Int? = null
    var isChecked: Boolean? = null

    var swipedDirection: Int = 0
    var swipedAction: Runnable? = null
    override var isDraggable = true
    override var isSwipeable = true

    override val type: Int
        get() = R.id.task_item_id

    override val layoutRes: Int
        get() = R.layout.task_item

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        holder.title.text = title
        if (description.isNullOrBlank()) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = description
        }
        if (hasDate!!) {
            holder.dateTime.visibility = View.VISIBLE
            if (dateTime!! < Calendar.getInstance()) {
                holder.dateTime.setTextColor(Color.RED)
            } else {
                holder.dateTime.setTextColor(Color.BLACK)
            }
            if (hasTime!!) {
                holder.dateTime.text =
                    SimpleDateFormat(
                        "dd MMM yyyy à HH:mm",
                        Locale.getDefault()
                    ).format(dateTime?.time!!)
            } else {
                holder.dateTime.text =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dateTime?.time!!)
            }
        } else {
            holder.dateTime.visibility = View.GONE
        }
        holder.color.setColorFilter(color!!)
        holder.checkBox.isChecked = isChecked!!
        holder.swipeContent.visibility = if (swipedDirection != 0) View.VISIBLE else View.GONE
        holder.itemContent.visibility = if (swipedDirection != 0) View.GONE else View.VISIBLE

        var swipedAction: CharSequence? = null
        var swipedText: CharSequence? = null

        if (swipedDirection != 0) {
            swipedAction = holder.itemView.context.getString(R.string.cancel)
            swipedText = holder.itemView.context.getString(R.string.deleted)
            holder.swipeContent.setBackgroundColor(Color.RED)
        }
        holder.swipedAction.text = swipedAction ?: ""
        holder.swipedText.text = swipedText ?: ""
        holder.swipedActionRunnable = this.swipedAction
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.title.text = null
        holder.description.text = null
        holder.dateTime.text = null
        holder.color.colorFilter = null
        holder.checkBox.isChecked = false
        holder.swipedAction.text = null
        holder.swipedText.text = null
        holder.swipedActionRunnable = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.taskTitle)
        var description: TextView = view.findViewById(R.id.taskDescription)
        var dateTime: TextView = view.findViewById(R.id.taskTime)
        var color: ImageView = view.findViewById(R.id.color)
        var checkBox: CheckBox = view.findViewById(R.id.taskCheckBox)
        var swipeContent: View = view.findViewById(R.id.swipeContent)
        var itemContent: View = view.findViewById(R.id.itemContent)
        var swipedText: TextView = view.findViewById(R.id.swipedText)
        var swipedAction: TextView = view.findViewById(R.id.swipedAction)

        var swipedActionRunnable: Runnable? = null

        init {
            swipedAction.setOnClickListener {
                swipedActionRunnable?.run()
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
                } else {
                    title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }
}

package com.example.planner.ui.tasks

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.R
import com.example.planner.utils.CalendarSerializer
import kotlinx.android.synthetic.main.task_item.view.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Serializable
data class Task(
    var title: String,
    var description: String,
    @Serializable(with = CalendarSerializer::class)
    var dateTime: Calendar,
    var hasDate: Boolean,
    var hasTime: Boolean,
    var isChecked: Boolean = false
)

class TaskAdapter(private var tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val format = Json { prettyPrint = true }
    private val fileName = "task_data"
    private val simpleDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val simpleDateTimeFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    private var showDoneTasks = false

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.itemView.apply {
            if (!showDoneTasks && currentTask.isChecked) {
                taskCard.visibility = View.GONE
                taskCard.layoutParams.height = 0
            } else {
                taskCard.visibility = View.VISIBLE
                taskCard.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            taskTitle.text = currentTask.title
            if (currentTask.isChecked) taskTitle.paintFlags =
                taskTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
            if (currentTask.description.isBlank()) {
                taskDescription.visibility = View.GONE
            } else {
                taskDescription.text = currentTask.description
                taskDescription.visibility = View.VISIBLE
            }
            if (currentTask.hasDate) {
                taskTimeChip.visibility = View.VISIBLE
                if (currentTask.hasTime) taskTimeChip.text =
                    simpleDateTimeFormat.format(currentTask.dateTime.time) else taskTimeChip.text =
                    simpleDateFormat.format(currentTask.dateTime.time)
            } else taskTimeChip.visibility = View.GONE
            taskCheckBox.isChecked = currentTask.isChecked
            taskCheckBox.setOnCheckedChangeListener { _, _ ->
                taskDone(currentTask, holder)
                saveTasks(context)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(task: Task) {
        tasks.add(0, task)
        notifyItemInserted(0)
    }

    private fun taskDone(task: Task, holder: TaskViewHolder) {
        val taskIndex = tasks.indexOf(task)
        var firstIndex = tasks.indexOfFirst { t -> t.isChecked } - 1
        val title = holder.itemView.taskTitle
        val card = holder.itemView.taskCard

        if (firstIndex == -2) firstIndex = tasks.lastIndex
        task.isChecked = !task.isChecked

        if (task.isChecked) {
            title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
            tasks.remove(task)
            tasks.add(firstIndex, task)
            if (!showDoneTasks) {
                card.visibility = View.GONE
                card.layoutParams.height = 0
            }
            notifyItemMoved(taskIndex, firstIndex)
        } else {
            title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tasks.remove(task)
            tasks.add(0, task)
            notifyItemMoved(taskIndex, 0)
        }
    }

    private fun deleteTask(task: Task) {
        val taskIndex = tasks.indexOf(task)
        tasks.remove(task)
        notifyItemRemoved(taskIndex)
    }

    fun fetchTasks(context: Context) {
        if (File(context.filesDir, fileName).exists()) {
            val fileContents = context.openFileInput(fileName).bufferedReader().readText()
            val data = format.decodeFromString<MutableList<Task>>(fileContents)
            tasks = data
        }
    }

    fun saveTasks(context: Context) {
        val fileContents = format.encodeToString(tasks)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun hideTasks() {
        showDoneTasks = false
        val test = tasks.count { it.isChecked }
        notifyItemRangeChanged(tasks.indexOfFirst { it.isChecked }, test)
    }

    fun showTasks() {
        showDoneTasks = true
        val test = tasks.count { it.isChecked }
        notifyItemRangeChanged(tasks.indexOfFirst { it.isChecked }, test)
    }
}

package com.example.planner.ui.tasks

import android.content.Context
import android.os.Looper
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
    var id: Long,
    var title: String,
    var description: String,
    @Serializable(with = CalendarSerializer::class)
    var dateTime: Calendar,
    var isChecked: Boolean = false
)

class TaskAdapter(private var tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val format = Json { prettyPrint = true }
    private val fileName = "task_data"

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        val simpleDateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        holder.itemView.apply {
            taskTitle.text = currentTask.title
            taskDescription.text = currentTask.description
            taskTimeChip.text = simpleDateFormat.format(currentTask.dateTime.time)
            taskCheckBox.isChecked = currentTask.isChecked
            taskCheckBox.setOnCheckedChangeListener { _, _ ->
                currentTask.isChecked = !currentTask.isChecked
                deleteTask()
                saveTasks(context)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun getLastId(): Long {
        return if (tasks.isNullOrEmpty()) 0 else tasks.last().id + 1
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    private fun deleteTask() {
        tasks.removeAll { task ->
            task.isChecked
        }
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            notifyDataSetChanged()
        }, 0)
    }

    fun saveTasks(context: Context) {
        val fileContents = format.encodeToString(tasks)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun fetchTasks(context: Context) {
        if (File(context.filesDir, fileName).exists()) {
            val fileContents = context.openFileInput(fileName).bufferedReader().readText()
            val data = format.decodeFromString<MutableList<Task>>(fileContents)
            tasks = data
        }
    }
}

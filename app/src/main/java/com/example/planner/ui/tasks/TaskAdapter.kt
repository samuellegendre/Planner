package com.example.planner.ui.tasks

import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.R
import kotlinx.android.synthetic.main.task_item.view.*

data class Task(
    var id: Long,
    var title: String,
    var isChecked: Boolean = false
)

class TaskAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.itemView.apply {
            taskTitle.text = currentTask.title
            taskCheckBox.isChecked = currentTask.isChecked
            taskCheckBox.setOnCheckedChangeListener { _, _ ->
                currentTask.isChecked = !currentTask.isChecked
                deleteTask()
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun getLastId(): Long {
        return if (tasks.isNullOrEmpty()) 0 else tasks.orEmpty().last().id + 1
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
}

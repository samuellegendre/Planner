package com.example.planner.utils

import android.content.Context
import com.example.planner.models.TaskItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.utils.DragDropUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

@Serializable
data class Task(
    var id: Long,
    var title: String,
    var description: String,
    @Serializable(with = CalendarSerializer::class)
    var calendar: Calendar,
    var hasDate: Boolean,
    var hasTime: Boolean,
    var color: Int,
    var isChecked: Boolean = false
)

class Tasks(
    private val context: Context,
    private val fastAdapter: FastAdapter<TaskItem>,
    private val itemAdapter: ItemAdapter<TaskItem>
) {
    private val fileName = "task_data"
    private val format = Json { prettyPrint = true }
    var tasks = mutableListOf<Task>()
    var items = mutableListOf<TaskItem>()

    private fun taskToItem(task: Task): TaskItem {
        val item = TaskItem()
        item.id = task.id
        item.title = task.title
        item.description = task.description
        item.dateTime = task.calendar
        item.hasDate = task.hasDate
        item.hasTime = task.hasTime
        item.color = task.color
        item.isChecked = task.isChecked
        return item
    }

    fun itemToTask(item: TaskItem): Task {
        return Task(
            item.id!!,
            item.title.toString(),
            item.description.toString(),
            item.dateTime!!,
            item.hasDate!!,
            item.hasTime!!,
            item.color!!,
            item.isChecked!!
        )
    }

    fun addTask(task: Task) {
        val item = taskToItem(task)
        tasks.add(0, task)
        items.add(0, item)
        itemAdapter.add(0, item)
        saveTasks()
    }

    fun updateTask(task: Task) {
        val item = taskToItem(task)
        val position = tasks.indexOf(tasks.first { it.id == task.id })
        val taskItem = itemAdapter.getAdapterItem(position)
        tasks[position] = task
        items[position] = item
        taskItem.title = item.title
        taskItem.description = item.description
        taskItem.dateTime = item.dateTime
        taskItem.hasDate = item.hasDate
        taskItem.hasTime = item.hasTime
        taskItem.color = item.color
        taskItem.isChecked = item.isChecked
        fastAdapter.notifyItemChanged(position)
        saveTasks()
    }

    fun removeTasks(list: List<Task>) {
        list.forEach {
            val position = tasks.indexOf(it)
            tasks.removeAt(position)
            items.removeAt(position)
            itemAdapter.itemFilter.remove(position)
        }
        saveTasks()
    }

    fun moveTask(oldPosition: Int, newPosition: Int) {
        val task = tasks[oldPosition]
        DragDropUtil.onMove(itemAdapter, oldPosition, newPosition)
        tasks.removeAt(oldPosition)
        tasks.add(newPosition, task)
        items.removeAt(oldPosition)
        items.add(newPosition, taskToItem(task))
        saveTasks()
    }

    fun sortTasks(taskComparator: Comparator<Task>, itemComparator: Comparator<TaskItem>) {
        tasks.sortWith(taskComparator)
        items.sortWith(itemComparator)
        itemAdapter.adapterItems.sortWith(itemComparator)
        fastAdapter.notifyDataSetChanged()
        saveTasks()
    }

    fun getLastId(): Long {
        return if (tasks.isEmpty()) 0 else tasks.maxOf { it.id }
    }

    private fun saveTasks() {
        val fileContents = format.encodeToString(tasks)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun fetchItems(): MutableList<TaskItem> {
        if (File(context.filesDir, fileName).exists()) {
            val fileContents = context.openFileInput(fileName).bufferedReader().readText()
            val data = format.decodeFromString<MutableList<Task>>(fileContents)
            tasks = data
        }
        items.clear()
        tasks.forEach {
            items.add(taskToItem(it))
        }
        return items
    }
}



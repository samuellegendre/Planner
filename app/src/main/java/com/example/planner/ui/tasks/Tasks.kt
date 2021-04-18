package com.example.planner.ui.tasks

import android.content.Context
import com.example.planner.utils.CalendarSerializer
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.utils.DragDropUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.FieldPosition
import java.util.*

@Serializable
data class Task(
    var title: String,
    var description: String,
    @Serializable(with = CalendarSerializer::class)
    var calendar: Calendar,
    var hasDate: Boolean,
    var hasTime: Boolean,
    var isChecked: Boolean = false
)

class Tasks(private val context: Context, private val fastItemAdapter: FastItemAdapter<TaskItem>) {
    private val fileName = "task_data"
    private val format = Json { prettyPrint = true }

    private var tasks = mutableListOf<Task>()
    var items = mutableListOf<TaskItem>()

    private fun taskToItem(task: Task): TaskItem {
        val taskItem = TaskItem()

        taskItem.title = task.title
        taskItem.description = task.description
        taskItem.dateTime = task.calendar
        taskItem.hasDate = task.hasDate
        taskItem.hasTime = task.hasTime
        taskItem.isChecked = task.isChecked
        return taskItem
    }

    private fun itemToTask(item: TaskItem): Task {
        return Task(
            item.title.toString(),
            item.description.toString(),
            item.dateTime!!,
            item.hasDate!!,
            item.hasTime!!,
            item.isChecked!!
        )
    }

    fun addTask(task: Task) {
        tasks.add(0, task)
        items.add(0, taskToItem(task))
        fastItemAdapter.set(items)
        save()
    }

    fun updateItem(position: Int, isChecked: Boolean) {
        val item = items[position]
        val task = tasks[position]
        item.isChecked = isChecked
        task.isChecked = isChecked
        save()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        tasks.removeAt(position)
        fastItemAdapter.itemFilter.remove(position)
        save()
    }

    fun moveItem(oldPosition: Int, newPosition: Int) {
        DragDropUtil.onMove(fastItemAdapter.itemAdapter, oldPosition, newPosition)

        items = fastItemAdapter.adapterItems.toMutableList()
        tasks.clear()
        items.forEach {
            tasks.add(itemToTask(it))
        }
        save()
    }

    fun save() {
        tasks.clear()
        items.forEach {
            tasks.add(itemToTask(it))
        }

        val fileContents = format.encodeToString(tasks)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun fetch(): MutableList<TaskItem> {
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



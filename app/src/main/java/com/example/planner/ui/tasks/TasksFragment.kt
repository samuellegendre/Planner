package com.example.planner.ui.tasks

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.NotificationsActivity
import com.example.planner.R
import com.example.planner.SearchableActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDragCallback
import java.util.*

class TasksFragment : Fragment(), AddTaskDialogFragment.AddTaskDialogListener,
    ModifyTaskDialogFragment.ModifyTaskDialogListener, ItemTouchCallback,
    SimpleSwipeCallback.ItemSwipeCallback {

    private lateinit var fastAdapter: FastAdapter<TaskItem>
    private lateinit var itemAdapter: ItemAdapter<TaskItem>
    private lateinit var tasks: Tasks

    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    private var hideTask = false
    private var inAscendingAlphabeticalOrder = false
    private var inAscendingDateOrder = false

    private val deleteHandler = Handler {
        val item = it.obj as TaskItem

        item.swipedAction = null
        val position = itemAdapter.getAdapterPosition(item)
        if (position != RecyclerView.NO_POSITION) {
            val list = mutableListOf<Task>()
            list.add(tasks.itemToTask(item))
            tasks.removeTasks(list)
        }
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val view: View = inflater.inflate(R.layout.fragment_tasks, container, false)

        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        fastAdapter.onClickListener =
            { _: View?, _: IAdapter<TaskItem>, item: TaskItem, _: Int ->
                val dialog = ModifyTaskDialogFragment(tasks.itemToTask(item))
                dialog.show(childFragmentManager, "modifyTask")
                false
            }

        fastAdapter.addEventHook(object : ClickEventHook<TaskItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return if (viewHolder is TaskItem.ViewHolder) {
                    viewHolder.checkBox
                } else {
                    null
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<TaskItem>,
                item: TaskItem
            ) {
                item.isChecked = !item.isChecked!!
                tasks.updateTask(tasks.itemToTask(item))
                if (hideTask) toggleHiddenTasks(!hideTask)
                if (item.isChecked!!) {
                    tasks.moveTask(position, tasks.items.lastIndex)
                } else {
                    tasks.moveTask(position, 0)
                }
                if (hideTask) toggleHiddenTasks(hideTask)
            }

        })

        tasks = Tasks(requireContext(), fastAdapter, itemAdapter)
        val recyclerView: RecyclerView = view.findViewById(R.id.taskRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastAdapter

        itemAdapter.add(tasks.fetchItems())

        val leaveBehindDrawableLeft =
            ResourcesCompat.getDrawable(requireContext().resources, R.drawable.ic_delete, null)

        touchCallback = SimpleSwipeDragCallback(
            this,
            this,
            leaveBehindDrawableLeft,
            ItemTouchHelper.LEFT,
            Color.RED
        )
            .withNotifyAllDrops(true)
            .withSensitivity(10f)
            .withSurfaceThreshold(0.8f)

        touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(recyclerView)

        fastAdapter.withSavedInstanceState(savedInstanceState)

        val addTaskButton: FloatingActionButton = view.findViewById(R.id.addTaskButton)

        addTaskButton.setOnClickListener {
            val dialog = AddTaskDialogFragment()
            dialog.show(childFragmentManager, "addTask")
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_tasks_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(requireContext(), SearchableActivity::class.java))
                true
            }
            R.id.notifications -> {
                startActivity(Intent(requireContext(), NotificationsActivity::class.java))
                tasks.save()
                true
            }
            R.id.hideTasks -> {
                item.isChecked = !item.isChecked
                hideTask = item.isChecked
                toggleHiddenTasks(hideTask)
                true
            }
            R.id.deleteCheckedTasks -> {
                val checkedTasks = mutableListOf<Task>()
                tasks.tasks.forEach { if (it.isChecked) checkedTasks.add(it) }

                if (hideTask) toggleHiddenTasks(!hideTask)
                tasks.removeTasks(checkedTasks)
                if (hideTask) toggleHiddenTasks(hideTask)
                true
            }
            R.id.actionSortByDueDate -> {
                inAscendingDateOrder = !inAscendingDateOrder
                if (inAscendingDateOrder) {
                    tasks.sortTasks(
                        compareBy<Task> { it.isChecked }.thenBy { it.calendar },
                        compareBy<TaskItem> { it.isChecked }.thenBy { it.dateTime })
                } else {
                    tasks.sortTasks(
                        compareBy<Task> { it.isChecked }.reversed().thenBy { it.calendar }
                            .reversed(),
                        compareBy<TaskItem> { it.isChecked }.reversed().thenBy { it.dateTime }
                            .reversed()
                    )
                }
                true
            }
            R.id.actionSortByAlphabetical -> {
                inAscendingAlphabeticalOrder = !inAscendingAlphabeticalOrder
                if (inAscendingAlphabeticalOrder) {
                    tasks.sortTasks(
                        compareBy<Task> { it.isChecked }.thenBy { it.title },
                        compareBy<TaskItem> { it.isChecked }.thenBy { it.title })
                } else {
                    tasks.sortTasks(
                        compareBy<Task> { it.isChecked }.reversed().thenBy { it.title }.reversed(),
                        compareBy<TaskItem> { it.isChecked }.reversed().thenBy { it.title }
                            .reversed()
                    )
                }
                true
            }
            else -> false
        }
    }

    override fun onAddTaskDialogPositiveClick(dialog: DialogFragment, task: Task) {
        task.id = tasks.getLastId() + 1
        tasks.addTask(task)
    }

    override fun onModifyTaskDialogPositiveClick(dialog: DialogFragment, task: Task) {
        tasks.updateTask(task)
    }

    override fun onModifyTaskDialogNegativeClick(dialog: DialogFragment, task: Task) {
        val list = mutableListOf<Task>()
        list.add(task)
        tasks.removeTasks(list)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        tasks.moveTask(oldPosition, newPosition)
        return true
    }

    override fun itemSwiped(position: Int, direction: Int) {
        val item = fastAdapter.getItem(position) ?: return
        item.swipedDirection = direction

        val message = Random().nextInt()
        deleteHandler.sendMessageDelayed(
            Message.obtain().apply { what = message; obj = item },
            3000
        )

        item.swipedAction = Runnable {
            deleteHandler.removeMessages(message)

            item.swipedDirection = 0
            val position = itemAdapter.getAdapterPosition(item)
            if (position != RecyclerView.NO_POSITION) {
                fastAdapter.notifyItemChanged(position)
            }
        }
        fastAdapter.notifyItemChanged(position)
    }

    private fun toggleHiddenTasks(isChecked: Boolean) {
        if (isChecked) {
            itemAdapter.setNewList(tasks.items.filter { it.isChecked == false })
        } else {
            itemAdapter.setNewList(tasks.items)
        }
        fastAdapter.notifyDataSetChanged()
    }
}
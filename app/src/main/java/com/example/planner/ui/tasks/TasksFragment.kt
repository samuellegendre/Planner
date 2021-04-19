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
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDragCallback
import java.util.*

class TasksFragment : Fragment(), AddTaskDialogFragment.AddTaskDialogListener,
    ModifyTaskDialogFragment.ModifyTaskDialogListener, ItemTouchCallback,
    SimpleSwipeCallback.ItemSwipeCallback {

    private lateinit var fastItemAdapter: FastItemAdapter<TaskItem>
    private lateinit var tasks: Tasks

    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    private val deleteHandler = Handler {
        val item = it.obj as TaskItem

        item.swipedAction = null
        val position = fastItemAdapter.getAdapterPosition(item)
        if (position != RecyclerView.NO_POSITION) {
            tasks.removeTask(tasks.itemToTask(item))
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

        fastItemAdapter = FastItemAdapter()

        fastItemAdapter.onClickListener =
            { _: View?, _: IAdapter<TaskItem>, item: TaskItem, _: Int ->
                val dialog = ModifyTaskDialogFragment(tasks.itemToTask(item))
                dialog.show(childFragmentManager, "modifyTask")
                false
            }

        fastItemAdapter.addEventHook(object : ClickEventHook<TaskItem>() {
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
                if (item.isChecked!!) {
                    tasks.moveTask(position, tasks.items.lastIndex)
                } else {
                    tasks.moveTask(position, 0)
                }
            }

        })

        tasks = Tasks(requireContext(), fastItemAdapter)
        val recyclerView: RecyclerView = view.findViewById(R.id.taskRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastItemAdapter

        fastItemAdapter.add(tasks.fetchItems())

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

        fastItemAdapter.withSavedInstanceState(savedInstanceState)

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
        tasks.removeTask(task)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        tasks.moveTask(oldPosition, newPosition)
        return true
    }

    override fun itemSwiped(position: Int, direction: Int) {
        val item = fastItemAdapter.getItem(position) ?: return
        item.swipedDirection = direction

        val message = Random().nextInt()
        deleteHandler.sendMessageDelayed(
            Message.obtain().apply { what = message; obj = item },
            3000
        )

        item.swipedAction = Runnable {
            deleteHandler.removeMessages(message)

            item.swipedDirection = 0
            val position = fastItemAdapter.getAdapterPosition(item)
            if (position != RecyclerView.NO_POSITION) {
                fastItemAdapter.notifyItemChanged(position)
            }
        }
        fastItemAdapter.notifyItemChanged(position)
    }
}
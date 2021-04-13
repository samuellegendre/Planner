package com.example.planner.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.planner.R
import kotlinx.android.synthetic.main.spinner_item.view.*

data class TeachingMethod(val color: Int, val title: Int)

object TeachingMethods {

    private val colors = intArrayOf(
        R.color.green,
        R.color.blue,
        R.color.yellow
    )

    private val titles = arrayOf(
        R.string.on_campus,
        R.string.online,
        R.string.asynchronous
    )

    var list: ArrayList<TeachingMethod>? = null
        get() {
            if (field != null) return field

            field = ArrayList()
            for (i in colors.indices) {

                val color = colors[i]
                val title = titles[i]

                val teachingMethod = TeachingMethod(color, title)
                field!!.add(teachingMethod)
            }

            return field
        }
}

class TeachingMethodArrayAdapter(context: Context, teachingMethodList: List<TeachingMethod>) :
    ArrayAdapter<TeachingMethod>(context, 0, teachingMethodList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {

        val teachingMethod = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)

        view.teachingMethodColor.setColorFilter(
            ContextCompat.getColor(
                context,
                teachingMethod!!.color
            )
        )
        view.teachingMethodTitle.setText(teachingMethod.title)

        return view
    }

}
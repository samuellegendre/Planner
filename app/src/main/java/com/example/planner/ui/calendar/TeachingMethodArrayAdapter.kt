package com.example.planner.ui.calendar

import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.example.planner.R
import kotlinx.android.synthetic.main.spinner_item.view.*


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
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        view.teachingMethodColor.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.parseColor(teachingMethod!!.color), BlendModeCompat.SRC_ATOP)
        view.teachingMethodTitle.text = teachingMethod.title

        return view
    }

}
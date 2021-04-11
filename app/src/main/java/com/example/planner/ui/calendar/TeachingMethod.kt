package com.example.planner.ui.calendar

data class TeachingMethod(val color: String, val title: String)

object TeachingMethods {

    private val colors = arrayOf(
        "#ecc249",
        "#30a8e5",
        "#9f51b4"
    )

    private val titles = arrayOf(
        "En présence",
        "À distance",
        "Hybride"
    )

    var list: ArrayList<TeachingMethod>? = null
    get () {
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
package com.androiddevs.RunTracker.Other

import android.content.Context
import com.androiddevs.RunTracker.database.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomeMarkerView(
    val runs:List<Run>,
    c:Context,
    layoutId:Int
) :MarkerView(c,layoutId){

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e==null){
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar= Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeed}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMetres / 1000f}km"
        tvDistance.text = distanceInKm

        tvDuration.text = TrackingUtility.getFormattedTime(run.timeInMillis,false)

        val caloriesBurned = "${run.burnedCalories}kcal"
        tvCaloriesBurned.text = caloriesBurned
    }
}
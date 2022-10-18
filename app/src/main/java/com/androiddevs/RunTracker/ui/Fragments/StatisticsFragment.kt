package com.androiddevs.RunTracker.ui.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevs.RunTracker.Other.CustomeMarkerView
import com.androiddevs.RunTracker.Other.TrackingUtility
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel
import com.androiddevs.RunTracker.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private val viewModel : StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SubscribeToObservers()
        setUpBarChart()
    }

    private fun setUpBarChart(){
        barChart.xAxis.apply {
            position= XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textColor=Color.WHITE
            axisLineColor=Color.WHITE
            setDrawLabels(false)
        }
        barChart.axisLeft.apply {
            setDrawGridLines(false)
            textColor=Color.WHITE
            axisLineColor=Color.WHITE
        }
        barChart.axisRight.apply {
            setDrawGridLines(false)
            textColor=Color.WHITE
            axisLineColor=Color.WHITE
        }
        barChart.apply {
            description.text="Avg Speed Over Time"
            legend.isEnabled=false
        }
    }

    private fun SubscribeToObservers(){
        viewModel.totalRunTime.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedTime(it)
                tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it/1000f
                val totalDistance = round(km*10)/10f
                val totalDistanceString = "${totalDistance}km"
                tvTotalDistance.text=totalDistanceString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCaloriesString = "${it}kcal"
                tvTotalCalories.text = totalCaloriesString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it*10)/10f
                val avgSpeedString = "${avgSpeed}km/h"
                tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(),it[i].avgSpeed) }
                val barDataSet = BarDataSet(allAvgSpeeds,"Avg Speed Over Time").apply {
                    valueTextColor=Color.WHITE
                    color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker = CustomeMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }
}
package com.androiddevs.RunTracker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.androiddevs.RunTracker.repositories.MainRepository
import dagger.hilt.android.AndroidEntryPoint


class StatisticsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    val totalRunTime = mainRepository.getTotalTimeInMillis()
    val totalDistance = mainRepository.getTotalDistanceInMetres()
    val totalCaloriesBurned = mainRepository.getTotalBurnedCalories()
    val totalAvgSpeed = mainRepository.getTotalAvgSpeed()

    val runsSortedByDate = mainRepository.getAllRunsSortedByTimeStamp()

}
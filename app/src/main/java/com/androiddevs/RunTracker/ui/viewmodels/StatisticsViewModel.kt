package com.androiddevs.RunTracker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.repositories.MainRepository
import com.androiddevs.RunTracker.repositories.iMainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


class StatisticsViewModel @ViewModelInject constructor(
    val mainRepository: iMainRepository
): ViewModel() {

    val totalRunTime = mainRepository.getTotalTimeInMillis()
    val totalDistance = mainRepository.getTotalDistanceInMetres()
    val totalCaloriesBurned = mainRepository.getTotalBurnedCalories()
    val totalAvgSpeed = mainRepository.getTotalAvgSpeed()

    val runsSortedByDate = mainRepository.getAllRunsSortedByTimeStamp()

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}
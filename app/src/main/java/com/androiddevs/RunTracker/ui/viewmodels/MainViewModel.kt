package com.androiddevs.RunTracker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.RunTracker.Other.SortType
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.repositories.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    private val runsSortedByDate = mainRepository.getAllRunsSortedByTimeStamp()
    private val runsSortedByRunningTime = mainRepository.getAllRunsSortedByTime()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByAVGSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
    private val runsSortedByCaloriesBurned= mainRepository.getAllRunsSortedByBurnedCalories()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init{
        runs.addSource(runsSortedByDate){result->
            if(sortType == SortType.DATE){
                result?.let {
                    runs.value=result
                }
            }
        }
        runs.addSource(runsSortedByRunningTime){result->
            if(sortType == SortType.TIME){
                result?.let {
                    runs.value=result
                }
            }
        }
        runs.addSource(runsSortedByDistance){result->
            if(sortType == SortType.DISTANCE){
                result?.let {
                    runs.value=result
                }
            }
        }
        runs.addSource(runsSortedByAVGSpeed){result->
            if(sortType == SortType.SPEED){
                result?.let {
                    runs.value=result
                }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned){result->
            if(sortType == SortType.CALORIESBURNED){
                result?.let {
                    runs.value=result
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runsSortedByDate.value?.let {
            runs.value= it
        }
        SortType.CALORIESBURNED -> runsSortedByCaloriesBurned.value?.let {
            runs.value= it
        }
        SortType.DISTANCE -> runsSortedByDistance.value?.let {
            runs.value= it
        }
        SortType.TIME -> runsSortedByRunningTime.value?.let {
            runs.value= it
        }
        SortType.SPEED -> runsSortedByAVGSpeed.value?.let {
            runs.value= it
        }
    }.also {
        this.sortType=sortType
    }





    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}
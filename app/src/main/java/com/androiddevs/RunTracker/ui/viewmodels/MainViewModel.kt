package com.androiddevs.RunTracker.ui.viewmodels

import android.app.Application
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.androiddevs.RunTracker.Other.Constants
import com.androiddevs.RunTracker.repositories.SortType
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.repositories.iMainRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @ViewModelInject constructor(
    private val mainRepository: iMainRepository, application: Application
): AndroidViewModel(application) {

     val runsSortedByDate = mainRepository.getAllRunsSortedByTimeStamp()
     val runsSortedByRunningTime = mainRepository.getAllRunsSortedByTime()
     val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
     val runsSortedByAVGSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
     val runsSortedByCaloriesBurned= mainRepository.getAllRunsSortedByBurnedCalories()



    var sortType = MutableLiveData<SortType>()
    init {
        sortType.postValue(SortType.DATE)
    }


    val sharedPref = mainRepository.getSharedPref(application)

    val runs:LiveData<List<Run>> = Transformations.switchMap(sortType){
        when(it){
            SortType.DATE -> {
                return@switchMap runsSortedByDate
            }
            SortType.CALORIESBURNED -> {
                return@switchMap runsSortedByCaloriesBurned
            }
            SortType.DISTANCE -> {
                return@switchMap runsSortedByDistance
            }
            SortType.TIME -> {
                return@switchMap runsSortedByRunningTime
            }
            SortType.SPEED -> {
                return@switchMap runsSortedByAVGSpeed
            }
            else -> {
                return@switchMap null
            }
        }
    }


    fun setSortType(sortType: SortType){
        this.sortType.postValue(sortType)
    }



    fun updateSharedPref(name:String,weight:String){
        sharedPref.edit()
            .putString(Constants.KEY_NAME,name)
            .putFloat(Constants.KEY_WEIGHT,weight.toFloat())
            .putBoolean(Constants.FIRST_TIME_TOGGLE,false)
            .apply()
    }



    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }



}
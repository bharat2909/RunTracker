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
    val mainRepository: iMainRepository, application: Application
): AndroidViewModel(application) {

     val runsSortedByDate = mainRepository.getAllRunsSortedByTimeStamp()
     val runsSortedByRunningTime = mainRepository.getAllRunsSortedByTime()
     val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
     val runsSortedByAVGSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
     val runsSortedByCaloriesBurned= mainRepository.getAllRunsSortedByBurnedCalories()

    //val runs = MediatorLiveData<List<Run>>()

    var sortType = MutableLiveData<SortType>()                  //SortType.DATE

//    @Inject
//    lateinit var sharedPref : SharedPreferences

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

//    init{
//        runs.addSource(runsSortedByDate){result->
//            if(sortType == SortType.DATE){
//                result?.let {
//                    runs.value=result
//                }
//            }
//        }
//        runs.addSource(runsSortedByRunningTime){result->
//            if(sortType == SortType.TIME){
//                result?.let {
//                    runs.value=result
//                }
//            }
//        }
//        runs.addSource(runsSortedByDistance){result->
//            if(sortType == SortType.DISTANCE){
//                result?.let {
//                    runs.value=result
//                }
//            }
//        }
//        runs.addSource(runsSortedByAVGSpeed){result->
//            if(sortType == SortType.SPEED){
//                result?.let {
//                    runs.value=result
//                }
//            }
//        }
//        runs.addSource(runsSortedByCaloriesBurned){result->
//            if(sortType == SortType.CALORIESBURNED){
//                result?.let {
//                    runs.value=result
//                }
//            }
//        }
//    }

//    fun sortRuns(sortType: SortType) = when(sortType){
//        SortType.DATE -> runsSortedByDate.value?.let {
//            runs.value= it
//        }
//        SortType.CALORIESBURNED -> runsSortedByCaloriesBurned.value?.let {
//            runs.value= it
//        }
//        SortType.DISTANCE -> runsSortedByDistance.value?.let {
//            runs.value= it
//        }
//        SortType.TIME -> runsSortedByRunningTime.value?.let {
//            runs.value= it
//        }
//        SortType.SPEED -> runsSortedByAVGSpeed.value?.let {
//            runs.value= it
//        }
//    }.also {
//        this.sortType=sortType
//    }

//    fun sortRuns(sortType: SortType):LiveData<List<Run>> {
//        when(sortType){
//            SortType.DATE -> {return runsSortedByDate}
//            SortType.CALORIESBURNED -> {return runsSortedByCaloriesBurned}
//            SortType.DISTANCE -> {return runsSortedByDistance}
//            SortType.TIME -> {return runsSortedByRunningTime}
//            SortType.SPEED -> {return runsSortedByAVGSpeed}
//        }
//    }

    fun useSharedPref(name:String,weight:String){
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
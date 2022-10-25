package com.androiddevs.RunTracker.repositories

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevs.RunTracker.Other.Constants.KEY_NAME
import com.androiddevs.RunTracker.database.Run

class FakeRepository : iMainRepository {

    private val listRuns = mutableListOf<Run>()

    private val observeSortedRunsByTime = MutableLiveData<List<Run>>(listRuns)
    private val observeTotalDistance = MutableLiveData<Int>()
    private val observeSortedRunsByBurnedCalories = MutableLiveData<List<Run>>(listRuns)
    private val observeSortedRuns = MutableLiveData<List<Run>>(listRuns)
    private val observeSortedRunsByAvgSpeed = MutableLiveData<List<Run>>(listRuns)
    private val observeSortedRunsByDistance = MutableLiveData<List<Run>>(listRuns)
    private val observeTotalTimeInMillis = MutableLiveData<Long>()
    private val observeTotalBurnedCalories = MutableLiveData<Int>()
    private val observeTotalAvgSpeed = MutableLiveData<Float>()


    private fun refreshLiveData(){
        observeSortedRunsByTime.postValue(sortListByTime())
        observeSortedRunsByDistance.postValue(sortListByDistance())
        observeSortedRuns.postValue(sortListByTimeStamp())
        observeSortedRunsByAvgSpeed.postValue(sortListByAvgSpeed())
        observeSortedRunsByBurnedCalories.postValue(sortListByBurnedCalories())
        observeTotalDistance.postValue(getTotalDistance())
        observeTotalTimeInMillis.postValue(getTotalTime())
        observeTotalAvgSpeed.postValue(getTotalAvgSpeed1())
        observeTotalBurnedCalories.postValue(getTotalBurnedCalories1())
    }

    private fun sortListByTime() : List<Run>{
        return listRuns.sortedBy { it.timeInMillis}
    }

    private fun sortListByDistance() : List<Run>{
        return listRuns.sortedBy { it.distanceInMetres }
    }

    private fun sortListByTimeStamp() : List<Run>{
        return listRuns.sortedBy { it.timeStamp}
    }

    private fun sortListByAvgSpeed() : List<Run>{
        return listRuns.sortedBy { it.avgSpeed}
    }

    private fun sortListByBurnedCalories() : List<Run>{
        return listRuns.sortedBy { it.burnedCalories}
    }

    private fun getTotalDistance():Int{
        return listRuns.sumBy { it.distanceInMetres.toInt() }
    }

    private fun getTotalTime():Long{
        return listRuns.sumByDouble { it.timeInMillis.toDouble() }.toLong()
    }

    private fun getTotalBurnedCalories1():Int{
        return listRuns.sumBy { it.burnedCalories.toInt() }
    }

    private fun getTotalAvgSpeed1():Float{
        return listRuns.sumByDouble { it.avgSpeed.toDouble() }.toFloat()
    }



    override suspend fun insertRun(run: Run) {
        listRuns.add(run)
        refreshLiveData()
    }

    override suspend fun deleteRun(run: Run) {
        listRuns.remove(run)
        refreshLiveData()
    }

    override fun getAllRunsSortedByTimeStamp(): LiveData<List<Run>> {
        return observeSortedRuns
    }

    override fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>> {
        return observeSortedRunsByAvgSpeed
    }

    override fun getAllRunsSortedByBurnedCalories(): LiveData<List<Run>> {
        return observeSortedRunsByBurnedCalories
    }

    override fun getTotalTimeInMillis(): LiveData<Long> {
        return observeTotalTimeInMillis
    }

    override fun getTotalAvgSpeed(): LiveData<Float> {
        return observeTotalAvgSpeed
    }

    override fun getTotalBurnedCalories(): LiveData<Int> {
        return observeTotalBurnedCalories
    }

    override fun getAllRunsSortedByTime(): LiveData<List<Run>> {
        return observeSortedRunsByTime
    }

    override fun getSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_NAME,MODE_PRIVATE)
    }

    override fun getTotalDistanceInMetres(): LiveData<Int> {
        return observeTotalDistance
    }

    override fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
        return observeSortedRunsByDistance
    }

}
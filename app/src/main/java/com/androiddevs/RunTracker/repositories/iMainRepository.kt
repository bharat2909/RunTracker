package com.androiddevs.RunTracker.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.androiddevs.RunTracker.database.Run


interface iMainRepository {

    suspend fun insertRun(run:Run)

    suspend fun deleteRun(run: Run)

    fun getAllRunsSortedByTime() : LiveData<List<Run>>

    fun getAllRunsSortedByDistance() : LiveData<List<Run>>

    fun getAllRunsSortedByTimeStamp() : LiveData<List<Run>>

    fun getAllRunsSortedByAvgSpeed() : LiveData<List<Run>>

    fun getAllRunsSortedByBurnedCalories() : LiveData<List<Run>>

    fun getTotalTimeInMillis() : LiveData<Long>

    fun getTotalAvgSpeed(): LiveData<Float>

    fun getTotalBurnedCalories() : LiveData<Int>

    fun getTotalDistanceInMetres() : LiveData<Int>

    fun getSharedPref(context:Context): SharedPreferences

}
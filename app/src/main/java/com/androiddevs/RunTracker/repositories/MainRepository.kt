package com.androiddevs.RunTracker.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.androiddevs.RunTracker.Other.Constants
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.database.RunDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class MainRepository @Inject constructor(
    val runDao: RunDao
) : iMainRepository{



    override suspend fun insertRun(run: Run) {
        runDao.insert(run)
    }

    override fun getSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.KEY_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun deleteRun(run: Run) {
       runDao.delete(run)
    }

    override fun getAllRunsSortedByTime(): LiveData<List<Run>> {
        return runDao.getAllRunsSortedByTime()
    }

    override fun getTotalDistanceInMetres(): LiveData<Int> {
        return runDao.getTotalDistanceInMetres()
    }



    override fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
       return runDao.getAllRunsSortedByDistance()
    }

    override fun getAllRunsSortedByTimeStamp(): LiveData<List<Run>> {
        return runDao.getAllRunsSortedByTimeStamp()
    }

    override fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>> {
        return runDao.getAllRunsSortedByAvgSpeed()
    }

    override fun getAllRunsSortedByBurnedCalories(): LiveData<List<Run>> {
        return runDao.getAllRunsSortedByBurnedCalories()
    }

    override fun getTotalTimeInMillis(): LiveData<Long> {
        return runDao.getTotalTimeInMillis()
    }

    override fun getTotalAvgSpeed(): LiveData<Float> {
        return runDao.getTotalAvgSpeed()
    }

    override fun getTotalBurnedCalories(): LiveData<Int> {
        return runDao.getTotalBurnedCalories()
    }


}
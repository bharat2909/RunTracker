package com.androiddevs.RunTracker.repositories

import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.database.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao: RunDao
) {

    suspend fun insertRun(run: Run) = runDao.insert(run)

    suspend fun deleteRun(run: Run) = runDao.delete(run)

    fun getAllRunsSortedByTime() = runDao.getAllRunsSortedByTime()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeStamp() = runDao.getAllRunsSortedByTimeStamp()

    fun getAllRunsSortedByBurnedCalories() = runDao.getAllRunsSortedByBurnedCalories()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getTotalDistanceInMetres() = runDao.getTotalDistanceInMetres()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    fun getTotalBurnedCalories() = runDao.getTotalBurnedCalories()
}
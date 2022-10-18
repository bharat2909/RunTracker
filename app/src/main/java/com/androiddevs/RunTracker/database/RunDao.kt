package com.androiddevs.RunTracker.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run:Run)

    @Delete
    suspend fun delete(run:Run)

    @Query("SELECT * FROM run_table ORDER BY timeStamp DESC")
    fun getAllRunsSortedByTimeStamp(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMetres DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTime(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY avgSpeed DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY burnedCalories DESC")
    fun getAllRunsSortedByBurnedCalories(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM run_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(distanceInMetres) FROM run_table")
    fun getTotalDistanceInMetres(): LiveData<Int>

    @Query("SELECT SUM(burnedCalories) FROM run_table")
    fun getTotalBurnedCalories(): LiveData<Int>

    @Query("SELECT AVG(avgSpeed) FROM run_table")
    fun getTotalAvgSpeed(): LiveData<Float>



}
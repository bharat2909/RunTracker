package com.androiddevs.RunTracker.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "run_table")
data class Run (
    var img: Bitmap?=null,
    var timeStamp:Long = 0L,
    var distanceInMetres:Int=0,
    var burnedCalories:Int=0,
    var timeInMillis:Long = 0L,
    var avgSpeed:Float = 0F
    ){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}
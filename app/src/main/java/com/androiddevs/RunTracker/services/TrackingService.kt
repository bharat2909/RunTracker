package com.androiddevs.RunTracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.androiddevs.RunTracker.Other.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.RunTracker.Other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.androiddevs.RunTracker.Other.Constants.ACTION_STOP_SERVICE
import com.androiddevs.RunTracker.Other.Constants.ACTION_TRACKING_FRAGMENT
import com.androiddevs.RunTracker.Other.Constants.NOTIFICATION_CHANNEL_ID
import com.androiddevs.RunTracker.Other.Constants.NOTIFICATION_CHANNEL_NAME
import com.androiddevs.RunTracker.Other.Constants.NOTIFICATION_ID
import com.androiddevs.RunTracker.Other.TrackingUtility
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.log

typealias line = MutableList<LatLng>
typealias lines = MutableList<line>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private var serviceKilled = false

    companion object{
        val timeRunInMillis=MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<lines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        curNotificationBuilder=baseNotificationBuilder
        isTracking.observe(this, Observer {
            updateLocation(it)
            updateNotificationStatus(it)
        })
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
                when(it.action){
                    ACTION_START_OR_RESUME_SERVICE ->{
                        if(isFirstRun){
                            startForegroundService()
                            isFirstRun = false
                        }else{
                            startTimer()
                            Log.e("Message:","Service Resumed")
                        }

                    }
                    ACTION_PAUSE_SERVICE -> {
                        Log.e("MESSAGE","SERVICE PAUSED")
                        pauseService()
                    }
                    ACTION_STOP_SERVICE -> {
                        Log.e("MESSAGE", "SERVICE STOPPED")
                        killService()
                    }
                    else -> Log.e("ERROR:","ERROR")
                }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    private var isTimerEnabled=false
    private var lapTime=0L
    private var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimeStamp=0L

    private fun startTimer(){
        addEmptyLine()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                lapTime=System.currentTimeMillis()-timeStarted

                timeRunInMillis.postValue(timeRun+lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimeStamp+1000L)
                {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimeStamp+=1000L
                }
                delay(50L)
            }
            timeRun+=lapTime
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasPermissions(this)){
                val request = LocationRequest().apply {
                    interval = 5000L
                    fastestInterval = 2000L
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(
                locationCallback
            )
        }
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun=true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    val locationCallback = object : LocationCallback(){                        //To add retrieved locations to list
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let { locations->
                    for(location in locations){
                        addPathPoint(location)
                        Log.e("Location::","${location.latitude},${location.longitude}")
                    }
                }
            }
        }
    }

    private fun updateNotificationStatus(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking){
            val pauseIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
            val resumeIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(curNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }

        if(!serviceKilled){
            curNotificationBuilder=baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp,notificationActionText,pendingIntent)

            notificationManager.notify(NOTIFICATION_ID,curNotificationBuilder.build())
        }
    }

    private fun addEmptyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location:Location){                        //To add coordinates at the end of list.
        val pos = LatLng(location.latitude, location.longitude)
        pathPoints.value?.apply {
            last().add(pos)
            pathPoints.postValue(this)
        }
    }

    private fun startForegroundService(){

        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if(!serviceKilled){
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedTime(it*1000))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }
        })
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

}
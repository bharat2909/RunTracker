package com.androiddevs.RunTracker

import android.app.Application
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication:Application() {

}
package com.androiddevs.RunTracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.androiddevs.RunTracker.Other.Constants.FIRST_TIME_TOGGLE
import com.androiddevs.RunTracker.Other.Constants.KEY_NAME
import com.androiddevs.RunTracker.Other.Constants.KEY_WEIGHT
import com.androiddevs.RunTracker.Other.Constants.RUN_DATABASE_NAME
import com.androiddevs.RunTracker.database.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext context:Context) = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        RUN_DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideRunDao(database: RunDatabase) = database.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(KEY_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(FIRST_TIME_TOGGLE,true)
}
package com.androiddevs.RunTracker.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.repositories.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class MainViewModelTest{

    private lateinit var viewModel:MainViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val application: Application = mock(Application::class.java)
    val dispatcher = TestCoroutineDispatcher()
    @Before
    fun setUp(){
        viewModel = MainViewModel(FakeRepository(),application)
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun insertTest()= runBlockingTest{
        val run = Run(null,0L,100,70,9000,10F)
        viewModel.insertRun(run)

        val allRuns = viewModel.runsSortedByDate.getOrAwaitValue()
        assertThat(allRuns).contains(run)
    }
}

private fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
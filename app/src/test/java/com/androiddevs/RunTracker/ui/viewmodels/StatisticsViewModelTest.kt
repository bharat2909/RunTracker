package com.androiddevs.RunTracker.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.repositories.FakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class StatisticsViewModelTest{
    private lateinit var viewModel:StatisticsViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()


    val dispatcher = TestCoroutineDispatcher()
    @Before
    fun setUp(){
        viewModel = StatisticsViewModel(FakeRepository())
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun getTotalDistanceTest()= runBlockingTest{
        val run = Run(null,0L,100,70,9000,10F)
        val run1 = Run(null,0L,800,90,19000,90F)
        viewModel.insertRun(run)
        viewModel.insertRun(run1)

        val totalDist = viewModel.totalDistance.getOrAwaitValue()
        assertThat(totalDist).isEqualTo(900)
    }

    @Test
    fun getTotalCaloriesBurnedTest()= runBlockingTest{
        val run = Run(null,0L,100,70,9000,10F)
        val run1 = Run(null,0L,800,90,19000,90F)
        val run2 = Run(null,0L,800,40,19000,90F)
        viewModel.insertRun(run)
        viewModel.insertRun(run1)
        viewModel.insertRun(run2)

        val totalBurned = viewModel.totalCaloriesBurned.getOrAwaitValue()
        assertThat(totalBurned).isEqualTo(200)
    }

    @Test
    fun getTotalTimeTest()= runBlockingTest{
        val run = Run(null,0L,100,70,9000,10F)
        val run1 = Run(null,0L,800,90,19000,90F)
        val run2 = Run(null,0L,800,40,29000,90F)
        viewModel.insertRun(run)
        viewModel.insertRun(run1)
        viewModel.insertRun(run2)

        val totalTime= viewModel.totalRunTime.getOrAwaitValue()
        assertThat(totalTime).isEqualTo(57000)
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
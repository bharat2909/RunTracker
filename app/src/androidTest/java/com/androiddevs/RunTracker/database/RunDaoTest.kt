package com.androiddevs.RunTracker.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Root
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class RunDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RunDatabase
    private lateinit var dao: RunDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunDatabase::class.java
           ).allowMainThreadQueries()
            .build()

        dao = database.getRunDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insert() = runBlockingTest {
        val run  = Run(null,0L,100,1500,10000,20F)
        dao.insert(run)

        val allruns = dao.getAllRunsSortedByTime().getOrAwaitValue()

        assertThat(allruns).contains(run)
    }

    @Test
    fun delete() = runBlockingTest {
        val run  = Run(null,0L,100,2500,10000,20F)
        dao.insert(run)
        dao.delete(run)

        val allruns = dao.getAllRunsSortedByTime().getOrAwaitValue()


        assertThat(allruns).doesNotContain(run)
    }

    @Test
    fun getTotalDistance() = runBlockingTest {
        val run  = Run(null,0L,100,2500,10000,20F)
        val run1  = Run(null,0L,100,2500,10000,20F)
        val run2  = Run(null,0L,100,2500,10000,20F)
        dao.insert(run)
        dao.insert(run1)
        dao.insert(run2)

        val totalDistance = dao.getTotalDistanceInMetres().getOrAwaitValue()

        assertThat(totalDistance).isEqualTo(300)
    }

    @Test
    fun getTotalTime() = runBlockingTest {
        val run  = Run(null,0L,100,2500,10000,20F)
        val run1  = Run(null,0L,100,2500,10000,20F)
        val run2  = Run(null,0L,100,2500,10000,20F)
        dao.insert(run)
        dao.insert(run1)
        dao.insert(run2)

        val totalTime = dao.getTotalTimeInMillis().getOrAwaitValue()

        assertThat(totalTime).isEqualTo(30000)
    }

    @Test
    fun getTotalBurnedCalories() = runBlockingTest {
        val run  = Run(null,0L,100,2500,10000,20F)
        val run1  = Run(null,0L,100,2000,10000,20F)
        val run2  = Run(null,0L,100,2200,10000,20F)
        dao.insert(run)
        dao.insert(run1)
        dao.insert(run2)

        val totalBurnedCalories = dao.getTotalBurnedCalories().getOrAwaitValue()

        assertThat(totalBurnedCalories).isEqualTo(6700)
    }

    @Test
    fun getAvgSpeed() = runBlockingTest {
        val run  = Run(null,0L,100,2500,10000,30F)
        val run1  = Run(null,0L,100,2500,10000,15F)
        val run2  = Run(null,0L,100,2500,10000,18F)
        dao.insert(run)
        dao.insert(run1)
        dao.insert(run2)

        val totalAvgSpeed = dao.getTotalAvgSpeed().getOrAwaitValue()

        assertThat(totalAvgSpeed).isEqualTo(21)
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

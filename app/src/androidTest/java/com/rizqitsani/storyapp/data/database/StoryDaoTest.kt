package com.rizqitsani.storyapp.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*

@ExperimentalCoroutinesApi
class StoryDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StoryDatabase
    private lateinit var dao: StoryDao
    private val sampleStories = DataDummy.generateDummyStories()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        dao = database.storyDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertStory() = runBlockingTest {
        dao.insertStory(sampleStories)
        val actualStories = dao.getStories().getOrAwaitValue()
        Assert.assertEquals(sampleStories[0].description, actualStories[0].description)
    }

    @Test
    fun deleteStory() = runBlockingTest {
        dao.insertStory(sampleStories)
        dao.deleteAll()
        val actualStories = dao.getStories().getOrAwaitValue()
        Assert.assertTrue(actualStories.isEmpty())
    }
}
package com.rizqitsani.storyapp.data.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqitsani.storyapp.domain.model.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<Story>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, Story>

    @Query("SELECT * FROM story")
    fun getStories(): LiveData<List<Story>>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}
package com.rizqitsani.storyapp

import com.rizqitsani.storyapp.data.remote.response.ListStoryItem
import com.rizqitsani.storyapp.data.remote.response.LoginResult
import com.rizqitsani.storyapp.domain.model.Story

object DataDummy {

    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "name $i",
                "desc $i",
                "photo $i",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "created_at $i",
                "name $i",
                "desc $i",
                0.0,
                "$i",
                0.0,
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyUser(): LoginResult {
        return LoginResult("Name", "123", generateDummyToken())
    }

    fun generateDummyToken(): String {
        return "eyj12ksljoij"
    }
}
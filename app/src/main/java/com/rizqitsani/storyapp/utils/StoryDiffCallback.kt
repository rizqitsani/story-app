package com.rizqitsani.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.rizqitsani.storyapp.domain.model.Story

class StoryDiffCallback(
    private val mOldStoryList: List<Story>,
    private val mNewStoryList: List<Story>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOldStoryList.size
    }

    override fun getNewListSize(): Int {
        return mNewStoryList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFavorite = mOldStoryList[oldItemPosition]
        val newFavorite = mNewStoryList[newItemPosition]
        return oldFavorite.name == newFavorite.name && oldFavorite.description == newFavorite.description && oldFavorite.photoUrl == newFavorite.photoUrl
    }
}
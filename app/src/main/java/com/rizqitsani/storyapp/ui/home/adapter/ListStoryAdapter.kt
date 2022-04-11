package com.rizqitsani.storyapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rizqitsani.storyapp.databinding.ItemRowStoryBinding
import com.rizqitsani.storyapp.domain.model.Story
import com.rizqitsani.storyapp.ui.home.HomeFragmentDirections
import com.rizqitsani.storyapp.utils.StoryDiffCallback

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {
    private val listStory = ArrayList<Story>()
    fun setListStory(listStory: List<Story>) {
        val diffCallback = StoryDiffCallback(this.listStory, listStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listStory.clear()
        this.listStory.addAll(listStory)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = listStory[position]

        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .apply(RequestOptions().override(550, 550))
            .into(holder.binding.imgItemPhoto)

        holder.binding.imgItemPhoto.transitionName = "image${listStory[position].id}"
        holder.binding.tvItemName.transitionName = "name${listStory[position].id}"
        holder.binding.tvItemDescription.transitionName = "description${listStory[position].id}"

        holder.binding.tvItemName.text = story.name
        holder.binding.tvItemDescription.text = story.description
        holder.itemView.setOnClickListener {
            val toStoryDetailFragment =
                HomeFragmentDirections.actionHomeFragmentToStoryDetailFragment(listStory[holder.adapterPosition])
            val extras = FragmentNavigatorExtras(
                holder.binding.imgItemPhoto to "image${listStory[position].id}",
                holder.binding.tvItemName to  "name${listStory[position].id}",
                holder.binding.tvItemDescription to "description${listStory[position].id}"
            )
            it.findNavController().navigate(toStoryDetailFragment, extras)
        }
    }

    override fun getItemCount(): Int = listStory.size
}
package com.rizqitsani.storyapp.ui.storydetail

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rizqitsani.storyapp.databinding.FragmentStoryDetailBinding
import com.rizqitsani.storyapp.ui.main.MainActivity

class StoryDetailFragment : Fragment() {
    private var _binding: FragmentStoryDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryDetailBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        val story = StoryDetailFragmentArgs.fromBundle(arguments as Bundle).story

        (activity as MainActivity).setActionBarTitle(story.name)

        binding?.imgStoryPhoto?.transitionName = "image${story.id}"
        binding?.tvStoryName?.transitionName = "name${story.id}"
        binding?.tvStoryDescription?.transitionName = "description${story.id}"

        Glide.with(this)
            .load(story.photoUrl)
            .apply(RequestOptions().override(550, 550))
            .into(binding?.imgStoryPhoto as ImageView)

        binding?.tvStoryName?.text = story.name
        binding?.tvStoryDescription?.text = story.description
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
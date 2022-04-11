package com.rizqitsani.storyapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizqitsani.storyapp.R
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.databinding.FragmentHomeBinding
import com.rizqitsani.storyapp.domain.model.Story
import com.rizqitsani.storyapp.ui.home.adapter.ListStoryAdapter
import com.rizqitsani.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            AuthPreferences.getInstance(context?.dataStore as DataStore<Preferences>)
        )
    }

    private lateinit var listStoryAdapter: ListStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setFullscreen(false)
        setHasOptionsMenu(true)

        setupRvAdapter()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        binding?.rvStory?.scrollToPosition(0)
    }

    private fun setupRvAdapter() {
        listStoryAdapter = ListStoryAdapter()

        val layoutManager = LinearLayoutManager(activity)
        binding?.rvStory?.layoutManager = layoutManager

        binding?.rvStory?.apply {
            this.adapter = listStoryAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun setupObserver() {
        viewModel.getCurrentUser().observe(viewLifecycleOwner) {
            if (it.token.isNotEmpty()) {
                viewModel.getStories(it.token)
            }
        }

        viewModel.listStory.observe(viewLifecycleOwner) {
            setListStory(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.message.observe(viewLifecycleOwner) {
            showMessage(it)
        }
    }

    private fun setListStory(listStory: List<Story>) {
        if (listStory.isNotEmpty()) {
            binding?.tvPlaceholder?.visibility = View.GONE
            binding?.rvStory?.visibility = View.VISIBLE
        } else {
            binding?.tvPlaceholder?.text = resources.getString(R.string.not_found)
            binding?.tvPlaceholder?.visibility = View.VISIBLE
            binding?.rvStory?.visibility = View.GONE
        }

        listStoryAdapter.setListStory(listStory)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        if (message != "") {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()
                true
            }
            else -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
                    || super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
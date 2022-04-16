package com.rizqitsani.storyapp.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rizqitsani.storyapp.R
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.databinding.FragmentMapBinding
import com.rizqitsani.storyapp.domain.model.Story
import com.rizqitsani.storyapp.ui.home.HomeViewModel
import com.rizqitsani.storyapp.ui.home.HomeViewModelFactory

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity())
    }

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupObserver()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupObserver() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it.token.isNotEmpty()) {
                viewModel.getStoriesWithLocation(it.token).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)
                                setListStoryMap(result.data)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                showMessage(getString(R.string.something_wrong))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setListStoryMap(storyList: List<Story>) {
        val firstLocation = LatLng(storyList[0].lat ?: 0.0, storyList[0].lon ?: 0.0)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15f))
        storyList.forEach {
            if (it.lat !== null && it.lon !== null)
                mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lon)).title(it.name))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.isVisible = isLoading
        binding?.map?.isVisible = !isLoading
    }

    private fun showMessage(message: String) {
        if (message != "") {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MapFragment"
    }
}
package com.rizqitsani.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.rizqitsani.storyapp.R
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.remote.response.AddStoryResponse
import com.rizqitsani.storyapp.databinding.FragmentAddStoryBinding
import com.rizqitsani.storyapp.ui.main.MainActivity
import com.rizqitsani.storyapp.utils.reduceFileImage
import com.rizqitsani.storyapp.utils.rotateBitmap
import com.rizqitsani.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding
    private val viewModel: AddStoryViewModel by viewModels {
        AddStoryViewModelFactory(requireActivity())
    }

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey") { _, bundle ->
            val myFile = bundle.getSerializable("picture") as File
            val isBackCamera = bundle.getBoolean("isBackCamera")

            getFile = myFile

            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)
            binding?.previewImageView?.setImageBitmap(result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddStoryBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setFullscreen(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPermission()
        setupAction()
    }

    private fun setupAction() {
        binding?.cameraButton?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_addStoryFragment_to_cameraFragment)
        )
        binding?.galleryButton?.setOnClickListener { startGallery() }
        binding?.uploadButton?.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        val file = reduceFileImage(getFile as File)

        val description =
            binding?.descriptionEditText?.text?.toString()
                ?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        viewModel.user.observe(viewLifecycleOwner) { user ->
            viewModel.addStory(user.token, imageMultipart, description as RequestBody)
                .observe(viewLifecycleOwner) {
                    addStoryObserver(it)
                }
        }
    }

    private fun addStoryObserver(result: Result<AddStoryResponse>) {
        when (result) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Success -> {
                showLoading(false)
                view?.findNavController()?.navigate(R.id.action_addStoryFragment_to_homeFragment)
            }
            is Result.Error -> {
                showLoading(false)
                showMessage(getString(R.string.something_wrong))
            }
        }
    }

    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(REQUIRED_PERMISSIONS)
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                println("Permission has been granted by user")
            }
            false -> {
                Toast.makeText(
                    activity,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        if (message != "") {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSIONS = Manifest.permission.CAMERA
    }
}
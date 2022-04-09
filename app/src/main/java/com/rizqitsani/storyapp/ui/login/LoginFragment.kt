package com.rizqitsani.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import com.rizqitsani.storyapp.R
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.databinding.FragmentLoginBinding
import com.rizqitsani.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            AuthPreferences.getInstance(context?.dataStore as DataStore<Preferences>)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setFullscreen(true)

        playAnimation()
        setupAction()
        setupObserver()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleTextView =
            ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(150)
        val messageTextView =
            ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1f).setDuration(150)
        val emailTextView =
            ObjectAnimator.ofFloat(binding?.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailEditText =
            ObjectAnimator.ofFloat(binding?.emailEditText, View.ALPHA, 1f).setDuration(150)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding?.passwordTextView, View.ALPHA, 1f).setDuration(150)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding?.passwordEditText, View.ALPHA, 1f).setDuration(150)
        val loginButton =
            ObjectAnimator.ofFloat(binding?.loginButton, View.ALPHA, 1f).setDuration(150)
        val signupButton =
            ObjectAnimator.ofFloat(binding?.signupButton, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(
                titleTextView,
                messageTextView,
                emailTextView,
                emailEditText,
                passwordTextView,
                passwordEditText,
                loginButton,
                signupButton
            )
            start()
        }
    }

    private fun setupAction() {
        binding?.signupButton?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signupFragment)
        )

        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()
            when {
                email.isEmpty() -> {
                    binding?.emailEditText?.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding?.passwordEditText?.error = "Masukkan password"
                }
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.message.observe(viewLifecycleOwner) {
            showMessage(it)
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
}
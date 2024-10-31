package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentLoginBinding
import com.ottistech.indespensa.ui.activity.MainActivity
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher
    private lateinit var viewModel : LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_login, container, false)
        viewModel = LoginViewModel(UserRepository(requireContext()))
        binding.model = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupSignupButton()

        var passwordVisibility = false
        binding.loginPasswordLayout.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.loginPasswordField, binding.loginPasswordLayout)
        }

    }

    private fun setupSignupButton() {
        binding.loginSignupButton.setOnClickListener {
            navigateToSignupScreen()
        }
    }

    private fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                if(feedback.code == FeedbackCode.SUCCESS) {
                    navigateToHome()
                    showToast(feedback.message)
                } else {
                    setError(feedback.message)
                }
            }
        }
    }

    private fun setError(error: String) {
        binding.loginError.visibility = View.VISIBLE
        binding.loginError.text = error
    }

    private fun navigateToSignupScreen() {
        val action = LoginFragmentDirections.actionLoginToSignup()
        findNavController().navigate(action)
    }

    private fun navigateToHome() {
        val currentActivity = requireActivity()
        val intent = Intent(currentActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }
}
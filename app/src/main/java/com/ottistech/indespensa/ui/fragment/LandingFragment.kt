package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.databinding.FragmentLandingBinding
import com.ottistech.indespensa.shared.AppAccountType

class LandingFragment : Fragment() {

    private lateinit var binding : FragmentLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.landingMainButton.setOnClickListener {
            navigateToSignupScreen(AppAccountType.PERSONAL)
        }
        binding.landingSecondButton.setOnClickListener {
            navigateToSignupScreen(AppAccountType.BUSINESS)
        }
    }

    private fun navigateToSignupScreen(signupType : AppAccountType) {
//        val action = LandingFragmentDirections.actionLandingToSignup(signupType)
        val action = LandingFragmentDirections.actionLandingToLogin()
        findNavController().navigate(action)
    }
}
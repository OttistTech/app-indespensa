package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.databinding.FragmentLandingBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.recyclerview.adapter.TextCarouselAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LandingFragment : Fragment() {

    private lateinit var binding : FragmentLandingBinding
    private var job: Job? = null

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

        val carouselItems = AppConstants.START_CAROUSEL_MESSAGES
        val carouselAdapter = TextCarouselAdapter(carouselItems)
        binding.landingViewPager.adapter = carouselAdapter
        binding.landingDotsIndicator.setViewPager2(binding.landingViewPager)
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(5000)
                val nextItem = (binding.landingViewPager.currentItem + 1) % carouselAdapter.itemCount
                binding.landingViewPager.currentItem = nextItem
            }
        }

//        val carouselPages = AppConstants.START_CAROUSEL_GIFS
//        val carouselPageAdapter = TextCarouselAdapter(carrouselPage=carouselPages)
//        binding.landingViewPager.adapter = carouselPageAdapter
//        binding.landingDotsIndicator.setViewPager2(binding.landingViewPager)
//        job = CoroutineScope(Dispatchers.Main).launch {
//            while (true) {
//                delay(5000)
//                val nextItem = (binding.landingViewPager.currentItem + 1) % carouselAdapter.itemCount
//                binding.landingViewPager.currentItem = nextItem
//            }
//        }

        binding.landingMainButton.setOnClickListener {
            navigateToSignupScreen(AppAccountType.PERSONAL)
        }
        binding.landingSecondButton.setOnClickListener {
            navigateToSignupScreen(AppAccountType.BUSINESS)
        }
        binding.landingLoginButton.setOnClickListener {
            navigateToLoginScreen()
        }
    }

    private fun navigateToSignupScreen(signupType : AppAccountType) {
        val action = LandingFragmentDirections.actionLandingToSignup(signupType)
        findNavController().navigate(action)
    }

    private fun navigateToLoginScreen() {
        val action = LandingFragmentDirections.actionLandingToLogin()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}

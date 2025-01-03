package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentProfileBinding
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.shared.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.RecipeAdapter
import com.ottistech.indespensa.ui.recyclerview.adapter.TextCarouselAdapter
import com.ottistech.indespensa.ui.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var recipeAdapter : RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ProfileViewModel(
            DashboardRepository(requireContext()),
            RecipeRepository(requireContext()),
            UserRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupLoadOnScroll()

        binding.profilePremiumButton.setOnClickListener {
            navigateToPremium()
        }

        binding.profileSettings.setOnClickListener {
            navigateToSettings()
        }

        if (requireContext().getCurrentUser().isPremium) {
            binding.profilePremiumInfo.visibility = View.GONE
            binding.profileCancelPremium.visibility = View.VISIBLE

            binding.profileCancelPremium.setOnClickListener {
                viewModel.fetchSwitchPremium()
            }
        } else {
            binding.profilePremiumInfo.visibility = View.VISIBLE
            binding.profileCancelPremium.visibility = View.GONE
        }

    }

    private fun setupRecyclerView() {
        with(binding.profileYourRecipesList) {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAdapter() {
        recipeAdapter = RecipeAdapter(
            context=requireContext()
        ) {
            navigateToRecipeDetails(it)
        }
    }

    private fun setupObservers() {
        viewModel.profileData.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.profileBigNumberData1.text = it.itemsInPantryCount.toString()
                binding.profileBigNumberLabel1.text = getString(R.string.profile_items_in_pantry)

                binding.profileBigNumberData2.text = it.purchasesMadeCount.toString()
                binding.profileBigNumberLabel2.text = getString(R.string.profile_purchases_made)

                binding.profileBigNumberData3.text = it.productsAlreadyExpiredCount.toString()
                binding.profileBigNumberLabel3.text = getString(R.string.profile_products_already_expired)

                binding.profileBigNumberData4.text = it.recipesMadeCount.toString()
                binding.profileBigNumberLabel4.text = getString(R.string.profile_recipes_made)
            }
        }

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                binding.profileYourRecipesList.visibility = View.VISIBLE
                recipeAdapter.addItems(it)
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(feedback)
            }
        }

        viewModel.carouselItems.observe(viewLifecycleOwner) { carouselItems ->
            val carouselAdapter = TextCarouselAdapter(carouselItems)
            binding.profileViewPager.adapter = carouselAdapter
            binding.profileDotsIndicator.setViewPager2(binding.profileViewPager)
        }

        viewModel.nextCarouselItem.observe(viewLifecycleOwner) { nextItem ->
            binding.profileViewPager.currentItem = nextItem
        }

        viewModel.fetchProfileData()
        viewModel.fetchRecipes()
    }

    private fun handleFeedback(feedback: Feedback) {
        when(feedback.feedbackId) {
            FeedbackId.GET_PROFILE_DATA -> {
                showToast(feedback.message)
            }
            FeedbackId.RECIPES_LIST -> {
                binding.profileMessage.text = feedback.message
                binding.profileMessage.visibility = View.VISIBLE
            }
            FeedbackId.SWITCH_PREMIUM -> {
                showToast(feedback.message)
                navigateToHome()
            }
        }
    }

    private fun setupLoadOnScroll() {
        binding.profileYourRecipesList.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (!binding.profileYourRecipesList.canScrollVertically(1) && scrollY > oldScrollY) {
                if (!viewModel.isRecipesLoading.value!!) {
                    viewModel.loadNextPage()
                }
            }
        }
    }

    private fun navigateToRecipeDetails(recipeId: Long) {
        val action = ProfileFragmentDirections.actionProfileToRecipeDetails(recipeId)
        findNavController().navigate(action)
    }

    private fun navigateToPremium() {
        val action = ProfileFragmentDirections.actionProfileToPremium()
        findNavController().navigate(action)
    }

    private fun navigateToSettings() {
        val action = ProfileFragmentDirections.actionProfileToUpdateProfile()
        findNavController().navigate(action)
    }

    private fun navigateToHome() {
        val action = ProfileFragmentDirections.actionProfileToHome()
        findNavController().navigate(action)
    }

}
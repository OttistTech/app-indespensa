package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentHomeBinding
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.ui.activity.AuthActivity
import com.ottistech.indespensa.ui.helpers.PermissionManager
import com.ottistech.indespensa.shared.formatAsString
import com.ottistech.indespensa.shared.showToast
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.CloseValidityAdapter
import com.ottistech.indespensa.ui.recyclerview.adapter.RecipeAdapter
import com.ottistech.indespensa.ui.viewmodel.HomeViewModel
import com.ottistech.indespensa.webclient.dto.dashboard.PersonalDashboardDTO

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel

    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var closeValidityAdapter: CloseValidityAdapter

    private lateinit var permissionManager: PermissionManager
    private val permissions = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        permissionManager = PermissionManager(
            fragment = this,
            permissions = permissions,
            onRequestSuccess = { showToast("Você será notificado de itens próximos da validade!") },
            onRequestFailure = { showToast("Permission is required") }
        )
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(
            UserRepository(requireContext()),
            DashboardRepository(requireContext()),
            RecipeRepository(requireContext()),
            PantryRepository(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAccountTypeRestrictions()
        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupLoadOnScroll()

        binding.homeFab.setOnClickListener {
            navigateToRecipeForm()
        }

        binding.homeAccessPantry.setOnClickListener {
            navigateToPantry()
        }

        if(!permissionManager.checkPermissions()) {
            permissionManager.requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchDashboardData()
        viewModel.fetchCloseValidityItems()
        viewModel.fetchRecommendedRecipes()
    }

    private fun setupObservers() {
        viewModel.dashboardData.observe(viewLifecycleOwner) { dashboard ->
            dashboard?.let {
                fillDashboard(it)
            }
        }
        viewModel.closeValidityItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                binding.homeCloseValidity.visibility = View.VISIBLE
                closeValidityAdapter.updateState(it)
            }
        }
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                binding.homeRecipeList.visibility = View.VISIBLE
                binding.homeRecipeMessage.visibility = View.GONE
                recipeAdapter.addItems(it)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isPageLoading ->
            handlePageLoading(isPageLoading)
        }
        viewModel.isRecipesLoading.observe(viewLifecycleOwner) { isRecipesLoading ->
            handleRecipesLoading(isRecipesLoading)
        }
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            if(feedback != null) {
                handleFeedback(feedback)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        if(feedback.code == FeedbackCode.UNAUTHORIZED) {
            showToast(feedback.message)
            navigateToAuth()
        }
        when(feedback.feedbackId) {
            FeedbackId.PERSONAL_DASHBOARD -> {
                handleDashboardFeedback(feedback)
            }
            FeedbackId.CLOSE_VALIDITY_ITEMS -> {
                handleCloseValidityItemsFeedback(feedback)
            }
            FeedbackId.RECIPES_LIST -> {
                handleRecipesListFeedback(feedback)
            }
        }
    }

    private fun handleRecipesListFeedback(feedback: Feedback) {
        with(binding) {
            homeRecipeList.visibility = View.GONE
            homeRecipeMessage.visibility = View.VISIBLE
            homeRecipeMessage.text = feedback.message
        }
    }

    private fun handleCloseValidityItemsFeedback(feedback: Feedback) {
        binding.homeCloseValidity.visibility = View.GONE
        if(feedback.code == FeedbackCode.UNHANDLED) {
            showToast(feedback.message)
        }
    }

    private fun handleDashboardFeedback(feedback: Feedback) {
        with(binding) {
            homeDashboard.visibility = View.GONE
            homeDashboardFeedback.visibility = View.VISIBLE
            homeDashboardFeedbackLogo.load(R.drawable.logo_error)
            homeDashboardFeedbackText.text = feedback.message
        }
    }

    private fun setupLoadOnScroll() {
        binding.homeScroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (!binding.homeScroll.canScrollVertically(1) && scrollY > oldScrollY) {
                if (!viewModel.isRecipesLoading.value!!) {
                    viewModel.loadNextRecipePage()
                }
            }
        }
    }

    private fun handleRecipesLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.homeRecipeProgressbar.visibility = View.VISIBLE
        } else {
            binding.homeRecipeProgressbar.visibility = View.GONE
        }
    }

    private fun handlePageLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.homeContent.visibility = View.GONE
            binding.homeProgressbar.visibility = View.VISIBLE
        } else {
            binding.homeContent.visibility = View.VISIBLE
            binding.homeProgressbar.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        with(binding.homeCloseValidityList) {
            adapter = closeValidityAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        with(binding.homeRecipeList) {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAdapter() {
        closeValidityAdapter = CloseValidityAdapter(
            context=requireContext()
        ) {
            navigateToProductDetails(it)
        }
        recipeAdapter = RecipeAdapter(
            context=requireContext()
        ) {
            navigateToRecipeDetails(it)
        }
    }

    private fun setupAccountTypeRestrictions() {
        if(viewModel.isUserPremium()) {
            binding.homeRecipeSearch.setOnClickListener {
                navigateToRecipeSearch()
            }
        } else {
            binding.homeRecipeSearchContainer.setEndIconDrawable(R.drawable.ic_locked)
            binding.homeRecipeSearch.setOnClickListener {
                showToast("Seja Premium para buscar receitas")
            }
        }
    }

    private fun fillDashboard(data: PersonalDashboardDTO) {
        with(binding) {
            homeDashboardFeedback.visibility = View.GONE
            homeDashboard.visibility = View.VISIBLE
            homeBigNumberData1.text = data.itemsInPantryCount.toString()
            homeBigNumberLabel1.text = getString(R.string.dashboard_personal_big_number_label_1)
            homeBigNumberData2.text = data.lastPurchaseDate?.formatAsString("dd/MM") ?: "-"
            homeBigNumberLabel2.text = getString(R.string.dashboard_personal_big_number_label_2)
            homeBigNumberData3.text = data.itemsCloseValidityDateCount.toString()
            homeBigNumberLabel3.text = getString(R.string.dashboard_personal_big_number_label_3)
            homeBigNumberData4.text = data.possibleRecipesInPantryCount.toString()
            homeBigNumberLabel4.text = getString(R.string.dashboard_personal_big_number_label_4)
        }
    }

    private fun navigateToPantry() {
        val action = HomeFragmentDirections.homeToPantry()
        findNavController().navigate(action)
    }

    private fun navigateToRecipeForm() {
        val action = HomeFragmentDirections.homeToRecipeForm()
        findNavController().navigate(action)
    }

    private fun navigateToRecipeSearch() {
        val action = HomeFragmentDirections.homeToRecipeSearch()
        findNavController().navigate(action)
    }

    private fun navigateToRecipeDetails(recipeId: Long) {
        val action = HomeFragmentDirections.homeToRecipeDetails(recipeId)
        findNavController().navigate(action)
    }

    private fun navigateToProductDetails(productId: Long) {
        val itemType = ProductItemType.PANTRY_ITEM
        val action = HomeFragmentDirections.homeToProductDetails(productId, itemType)
        findNavController().navigate(action)
    }

    private fun navigateToAuth() {
        val currentActivity = requireActivity()
        val intent = Intent(currentActivity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }
}
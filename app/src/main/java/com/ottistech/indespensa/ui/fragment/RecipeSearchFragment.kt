package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.databinding.FragmentRecipeSearchBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.dialog.RecipeFiltersDialogCreator
import com.ottistech.indespensa.ui.recyclerview.adapter.FilterAdapter
import com.ottistech.indespensa.ui.recyclerview.adapter.RecipeAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeSearchViewModel
import com.ottistech.indespensa.ui.viewmodel.state.RecipeSearchFiltersState

class RecipeSearchFragment : Fragment() {

    private lateinit var binding : FragmentRecipeSearchBinding
    private lateinit var recipeAdapter : RecipeAdapter
    private lateinit var filterAdapter : FilterAdapter<Any>
    private lateinit var viewModel : RecipeSearchViewModel
    private lateinit var dialogCreator: RecipeFiltersDialogCreator

    private val defaultFilters = AppConstants.DEFAULT_RECIPE_FILTERS.map { it.copy() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeSearchBinding.inflate(inflater, container, false)
        viewModel = RecipeSearchViewModel(RecipeRepository(requireContext()))
        dialogCreator = RecipeFiltersDialogCreator(
            requireContext(),
            viewModel
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupLoadOnScroll()
        setupSearchBar()

        binding.recipeSearchOpenFiltersButton.setOnClickListener {
            dialogCreator.showDialog { appliedFilters ->
                recipeAdapter.clearItems()
                viewModel.applyFilters(*appliedFilters)
                updateFiltersUi()
            }
        }

        binding.recipeSearchClearFiltersButton.setOnClickListener {
            binding.recipeSearchSearchbarInput.text = null
            filterAdapter.clearFilters()
            recipeAdapter.clearItems()
            viewModel.clearFilters()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.recipeSearchSearchbarInput.setText(viewModel.filters.value?.queryText)
        recipeAdapter.clearItems()
        viewModel.fetchRecipes()
    }

    private fun setupAdapter() {
        recipeAdapter = RecipeAdapter(
            context=requireContext()
        ) {
            navigateToRecipeDetails(it)
        }
        filterAdapter = FilterAdapter(
            context=requireContext(),
            filters=defaultFilters,
            isExclusive=false
        ) {
            recipeAdapter.clearItems()
            viewModel.applyFilters(it)
        }
    }

    private fun setupRecyclerView() {
        with(binding.recipeSearchRecipeList) {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        with(binding.recipeSearchDefaultFiltersList) {
            adapter = filterAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun setupLoadOnScroll() {
        binding.recipeSearchScroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (!binding.recipeSearchScroll.canScrollVertically(1) && scrollY > oldScrollY) {
                if (!viewModel.isLoading.value!!) {
                    viewModel.loadNextPage()
                }
            }
        }
    }

    private fun setupSearchBar() {
        binding.recipeSearchSearchbarInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if(text.isNotBlank()) {
                    recipeAdapter.clearItems()
                    viewModel.applyQueryText(text)
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                recipeAdapter.addItems(it)
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            binding.recipeSearchMessage.apply {
                if(feedback != null) {
                    if(
                        !viewModel.isLoading.value!! &&
                        viewModel.recipes.value.isNullOrEmpty()
                    ) {
                        text = feedback.message
                        visibility = View.VISIBLE
                    }
                } else {
                    visibility = View.GONE
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.recipeSearchProgressbar.visibility =
                if(isLoading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun updateFiltersUi() {
        defaultFilters.forEach { it.isSelected = false }
        with(viewModel.filters.value!!) {
            if (this.level != RecipeSearchFiltersState.LEVEL_DEFAULT_VALUE) {
                defaultFilters.find { it.value == this.level }?.isSelected = true
            }
            if (this.availability != RecipeSearchFiltersState.AVAILABILITY_DEFAULT_VALUE) {
                defaultFilters.find { it.value == this.availability }?.isSelected = true
            }
            if (this.preparationTimeRange != RecipeSearchFiltersState.PREPARATION_TIME_RANGE_DEFAULT_VALUE) {
                defaultFilters.find { it.value == this.preparationTimeRange }?.isSelected = true
            }
        }
        filterAdapter.updateState(defaultFilters)
    }

    private fun navigateToRecipeDetails(recipeId: Long) {
        val action = RecipeSearchFragmentDirections.recipeSearchToRecipeDetails(recipeId)
        findNavController().navigate(action)
    }
}


package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.databinding.FragmentRecipeListBinding
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.RecipeAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeListViewModel

class RecipeListFragment : Fragment() {

    private lateinit var binding : FragmentRecipeListBinding
    private lateinit var viewModel: RecipeListViewModel
    private lateinit var recipeAdapter : RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        viewModel = RecipeListViewModel(
            RecipeRepository(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupLoadOnScroll()
    }

    override fun onResume() {
        super.onResume()
        recipeAdapter.clearItems()
        viewModel.fetchRecipes()
    }

    private fun setupRecyclerView() {
        with(binding.recipeListList) {
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
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                binding.recipeListList.visibility = View.VISIBLE
                recipeAdapter.addItems(it)
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(feedback)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        when(feedback.feedbackId) {
            FeedbackId.RECIPES_LIST -> {
                binding.recipeListMessage.text = feedback.message
                binding.recipeListMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun setupLoadOnScroll() {
        binding.recipeListList.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (!binding.recipeListList.canScrollVertically(1) && scrollY > oldScrollY) {
                if (!viewModel.isLoading.value!!) {
                    viewModel.loadNextPage()
                }
            }
        }
    }

    private fun navigateToRecipeDetails(recipeId: Long) {
        val action = ProfileFragmentDirections.actionProfileToRecipeDetails(recipeId)
        findNavController().navigate(action)
    }
}
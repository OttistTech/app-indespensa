package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.databinding.FragmentRecipeDetailsBinding
import com.ottistech.indespensa.ui.dialog.RatingDialogCreator
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.recyclerview.adapter.RecipeDetailsAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeDetailsViewModel
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeDetailsDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDetailsDTO

class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var adapter: RecipeDetailsAdapter
    private lateinit var viewModel: RecipeDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        adapter = setupAdapter(emptyList())
        viewModel = RecipeDetailsViewModel(RecipeRepository(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        // TODO: get this recipe_id passed by args
        val recipeId = 19L
        viewModel.fetchRecipeDetails(recipeId)

        val ratingDialogCreator = RatingDialogCreator(requireContext())
        binding.recipeDetailsPrepareButton.setOnClickListener {
            ratingDialogCreator.showRatingDialog { rating ->

                val rateRecipeDTO = RateRecipeRequestDTO(
                    userId = requireContext().getCurrentUser().userId,
                    numStars = rating
                )

                viewModel.handleRatingClick(
                    rateRecipeRequestDTO = rateRecipeDTO,
                    recipeId = recipeId,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Receita avaliada com sucesso!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    },
                    onError = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun setupAdapter(ingredientsList: List<RecipeIngredientDetailsDTO>): RecipeDetailsAdapter {
        return RecipeDetailsAdapter(
            context = requireContext(),
            ingredientsList = ingredientsList
        )
    }

    private fun setupRecyclerView() {
        binding.recipeDetailsItemsList.layoutManager = LinearLayoutManager(requireContext())
        binding.recipeDetailsItemsList.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.recipeDetails.observe(viewLifecycleOwner) { recipeDetails ->
            recipeDetails?.let {
                updateRecipeUI(it)
            } ?: run {
                Toast.makeText(requireContext(), "Recipe details not found", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateRecipeUI(recipeDetails: RecipeDetailsDTO) {
        binding.recipeDetailsImage.loadImage(recipeDetails.imageUrl)

        val preparationTime = recipeDetails.preparationTime.toString() + "min"
        val amountIngredients = recipeDetails.amountIngredients
        val amountInPantry = recipeDetails.amountInPantry
        val spanExcerpt = "$amountInPantry/$amountIngredients"

        when (recipeDetails.level) {
            "Fácil" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.green)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.easy_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)
                binding.recipeDetailsPrepareMethod.text = makeSpanText(
                    getString(R.string.preparation_mode, preparationTime),
                    preparationTime,
                    color
                )

                binding.recipeDetailsIngredientsQuantity.text = makeSpanText(
                    getString(R.string.ingredients_count, amountInPantry, amountIngredients),
                    spanExcerpt,
                    color
                )
            }
            "Inter" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.secondary)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.inter_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)
                binding.recipeDetailsPrepareMethod.text = makeSpanText(
                    getString(R.string.preparation_mode, preparationTime),
                    preparationTime,
                    color
                )

                binding.recipeDetailsIngredientsQuantity.text = makeSpanText(
                    getString(R.string.ingredients_count, amountInPantry, amountIngredients),
                    spanExcerpt,
                    color
                )
            }
            "Complexo" -> {
                val color = ContextCompat.getColor(requireContext(), R.color.red)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.complex_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)

                binding.recipeDetailsPrepareMethod.text = makeSpanText(
                    getString(R.string.preparation_mode, preparationTime),
                    preparationTime,
                    color
                )

                binding.recipeDetailsIngredientsQuantity.text = makeSpanText(
                    getString(R.string.ingredients_count, amountInPantry, amountIngredients),
                    spanExcerpt,
                    color
                )
            }
        }

        binding.recipeDetailsFood.text = recipeDetails.level
        binding.recipeDetailsTitle.text = recipeDetails.title

        binding.ratingBar.rating = recipeDetails.numStars.toFloat()

        binding.recipeDetailsDescription.text = recipeDetails.description

        adapter = setupAdapter(recipeDetails.ingredients)
        binding.recipeDetailsItemsList.adapter = adapter

        binding.recipeDetailsPrepareMethodStepByStep.text = recipeDetails.preparationMethod
    }

    private fun navigateToHome() {
        val action = RecipeDetailsFragmentDirections.recipeDetailsToHome()
        findNavController().navigate(action)
    }

}

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
import com.ottistech.indespensa.shared.RecipeLevels
import com.ottistech.indespensa.ui.UiMode
import com.ottistech.indespensa.ui.dialog.RatingDialogCreator
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.recyclerview.adapter.IngredientAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeDetailsViewModel
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO

class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var adapter: IngredientAdapter
    private lateinit var viewModel: RecipeDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        adapter = setupAdapter()
        viewModel = RecipeDetailsViewModel(RecipeRepository(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        // TODO: get this recipe_id passed by args
        val recipeId = 20L

        binding.recipeDetailsContent.visibility = View.GONE
        binding.recipeDetailsProgressbar.visibility = View.VISIBLE
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

    private fun setupAdapter(): IngredientAdapter {
        return IngredientAdapter(
            context=requireContext(),
            mode=UiMode.READ
        )
    }

    private fun setupRecyclerView() {
        binding.recipeDetailsItemsList.adapter = adapter
        binding.recipeDetailsItemsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.recipeDetails.observe(viewLifecycleOwner) { recipeDetails ->

            binding.recipeDetailsProgressbar.visibility = View.GONE
            recipeDetails?.let {
                updateRecipeUI(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.message?.let {
                showToast(it)
                navigateToHome()
            }
        }
    }

    private fun updateRecipeUI(recipeDetails: RecipeFullDTO) {
        binding.recipeDetailsImage.loadImage(recipeDetails.imageUrl)

        val amountIngredients = recipeDetails.amountIngredients
        val amountInPantry = recipeDetails.amountInPantry
        val spanExcerpt = "($amountInPantry/$amountIngredients)"

        val ingredientsQuantityColor = when ((amountInPantry * 100) / amountIngredients) {
            in 0..80 -> ContextCompat.getColor(requireContext(), R.color.red)
            in 81..99 -> ContextCompat.getColor(requireContext(), R.color.secondary)
            100 -> ContextCompat.getColor(requireContext(), R.color.green)
            else -> ContextCompat.getColor(requireContext(), R.color.green)
        }

        binding.recipeDetailsIngredientsQuantity.text = makeSpanText(
            getString(R.string.ingredients_count, amountInPantry, amountIngredients),
            spanExcerpt,
            ingredientsQuantityColor
        )

        when (recipeDetails.level) {
            RecipeLevels.EASY.level -> {
                val color = ContextCompat.getColor(requireContext(), R.color.green)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.easy_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)

            }
            RecipeLevels.INTER.level -> {
                val color = ContextCompat.getColor(requireContext(), R.color.secondary)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.inter_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)

            }
            RecipeLevels.COMPLEX.level -> {
                val color = ContextCompat.getColor(requireContext(), R.color.red)

                binding.recipeDetailsFood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.complex_difficulty, 0, 0, 0)
                binding.recipeDetailsFood.setTextColor(color)
            }
        }

        val preparationTime = recipeDetails.preparationTime.toString() + "min"
        val prepareMethodColor = ContextCompat.getColor(requireContext(), R.color.green)
        binding.recipeDetailsPrepareMethod.text = makeSpanText(
            getString(R.string.preparation_mode, preparationTime),
            preparationTime,
            prepareMethodColor
        )

        binding.recipeDetailsFood.text = recipeDetails.level
        binding.recipeDetailsTitle.text = recipeDetails.title

        binding.recipeDetailsRatingBar.rating = recipeDetails.numStars.toFloat()

        binding.recipeDetailsDescription.text = recipeDetails.description

        adapter.updateState(recipeDetails.ingredients)
        binding.recipeDetailsItemsList.adapter = adapter

        binding.recipeDetailsPrepareMethodStepByStep.text = recipeDetails.preparationMethod

        binding.recipeDetailsContent.visibility = View.VISIBLE
    }

    private fun navigateToHome() {
        val action = RecipeDetailsFragmentDirections.recipeDetailsToHome()
        findNavController().navigate(action)
    }

}

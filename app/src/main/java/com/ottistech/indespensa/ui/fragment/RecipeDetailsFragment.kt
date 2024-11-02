package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.databinding.FragmentRecipeDetailsBinding
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.ui.UiMode
import com.ottistech.indespensa.ui.dialog.RatingDialogCreator
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.IngredientAdapter
import com.ottistech.indespensa.ui.viewmodel.RecipeDetailsViewModel
import com.ottistech.indespensa.webclient.dto.recipe.RateRecipeRequestDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeFullDTO

class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var adapter: IngredientAdapter
    private lateinit var viewModel: RecipeDetailsViewModel
    private val args : RecipeDetailsFragmentArgs by navArgs<RecipeDetailsFragmentArgs>()

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

        binding.recipeDetailsContent.visibility = View.GONE
        binding.recipeDetailsProgressbar.visibility = View.VISIBLE
        viewModel.fetchRecipeDetails(args.recipeId)

        val ratingDialogCreator = RatingDialogCreator(requireContext())
        binding.recipeDetailsPrepareButton.setOnClickListener {
            ratingDialogCreator.showRatingDialog { rating ->

                val rateRecipeDTO = RateRecipeRequestDTO(
                    userId = requireContext().getCurrentUser().userId,
                    numStars = rating
                )

                viewModel.handleRatingClick(
                    rateRecipeRequestDTO = rateRecipeDTO,
                    recipeId = args.recipeId,
                    onSuccess = {
                        showToast("Receita avaliada com sucesso!")
                        popupBackstack()
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

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        if(feedback.feedbackId == FeedbackId.GET_RECIPE_DETAILS) {
            showToast(feedback.message)
            popupBackstack()
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
            RecipeLevel.EASY -> {
                val color = ContextCompat.getColor(requireContext(), R.color.green)

                binding.recipeDetailsLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_easy, 0, 0, 0)
                binding.recipeDetailsLevel.setTextColor(color)

            }
            RecipeLevel.INTER -> {
                val color = ContextCompat.getColor(requireContext(), R.color.secondary)

                binding.recipeDetailsLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inter, 0, 0, 0)
                binding.recipeDetailsLevel.setTextColor(color)

            }
            RecipeLevel.ADVANCED -> {
                val color = ContextCompat.getColor(requireContext(), R.color.red)

                binding.recipeDetailsLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_advanced, 0, 0, 0)
                binding.recipeDetailsLevel.setTextColor(color)
            }
        }

        val preparationTime = recipeDetails.preparationTime.toString() + "min"
        val prepareMethodColor = ContextCompat.getColor(requireContext(), R.color.green)
        binding.recipeDetailsPrepareMethod.text = makeSpanText(
            getString(R.string.preparation_mode, preparationTime),
            preparationTime,
            prepareMethodColor
        )

        binding.recipeDetailsLevel.text = recipeDetails.level.displayName
        binding.recipeDetailsTitle.text = recipeDetails.title
        binding.recipeDetailsRatingBar.rating = recipeDetails.numStars

        binding.recipeDetailsDescription.text = recipeDetails.description

        adapter.updateState(recipeDetails.ingredients)
        binding.recipeDetailsItemsList.adapter = adapter

        binding.recipeDetailsPrepareMethodStepByStep.text = recipeDetails.preparationMethod

        binding.recipeDetailsContent.visibility = View.VISIBLE
    }

    private fun popupBackstack() {
        findNavController().popBackStack(R.id.nav_home, false)
    }

}

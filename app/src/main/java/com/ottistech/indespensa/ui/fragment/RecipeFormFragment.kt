package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.RecipeRepository
import com.ottistech.indespensa.databinding.FragmentRecipeFormBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.UiMode
import com.ottistech.indespensa.ui.dialog.IngredientDialogCreator
import com.ottistech.indespensa.shared.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.recyclerview.adapter.IngredientAdapter
import com.ottistech.indespensa.ui.recyclerview.helper.ingredientItemTouchHelper
import com.ottistech.indespensa.ui.viewmodel.RecipeFormViewModel

class RecipeFormFragment : Fragment() {

    private lateinit var binding: FragmentRecipeFormBinding
    private lateinit var viewModel: RecipeFormViewModel
    private lateinit var dialogCreator: IngredientDialogCreator
    private lateinit var ingredientsAdapter: IngredientAdapter

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUri = result.data?.data
        if(imageUri != null) {
            binding.recipeFormImage.setImageURI(imageUri)
            viewModel.setNewProductImageBitmap(binding.recipeFormImage.drawable.toBitmap())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogCreator = IngredientDialogCreator(requireContext())
        binding = FragmentRecipeFormBinding.inflate(inflater, container, false)
        viewModel = RecipeFormViewModel(
            RecipeRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupObservers()
        setupRecyclerView()
        setupValidationListeners()
        setupImageInput()
        setupAddIngredientButton()
        setupLevelSelect()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.recipeFormBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.recipe_form_dest, true)
    }

    private fun setupLevelSelect() {
        val levelsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.RECIPE_LEVELS)
        binding.recipeFormSelectLevel.setAdapter(levelsAdapter)
    }

    private fun setupAddIngredientButton() {
        binding.recipeFormAddIngredientButton.setOnClickListener {
            dialogCreator.showDialog(viewLifecycleOwner) { ingredient ->
                ingredientsAdapter.addItem(ingredient)
            }
        }
    }

    private fun setupImageInput() {
        binding.recipeFormImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }
    }

    private fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(feedback)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        showToast(feedback.message)
        if(feedback.code == FeedbackCode.SUCCESS) {
            findNavController().popBackStack(R.id.nav_home, false)
        }
    }

    private fun setupValidationListeners() {
        binding.recipeFormNameField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validName() }
        }
        binding.recipeFormDescriptionField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validDescription() }
        }
        binding.recipeFormTimeField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validTime() }
        }
        binding.recipeFormSelectLevel.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validLevel() }
        }
        binding.recipeFormPreparationMethodField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validPreparationMethod() }
        }
    }

    private fun setupAdapter() {
        ingredientsAdapter = IngredientAdapter(
            context=requireContext(),
            mode=UiMode.EDIT,
            onItemRemoved={ position ->
                viewModel.removeIngredient(position)
                showToast("Ingrediente removido")
            },
            onItemAdded={ item ->
                viewModel.addIngredient(item)
                showToast("Ingrediente adicionado")
            }
        )
    }

    private fun setupRecyclerView() {
        with(binding.recipeFormIngredientsList) {
            this.adapter = ingredientsAdapter
            this.layoutManager = LinearLayoutManager(context)
            ItemTouchHelper(ingredientItemTouchHelper).attachToRecyclerView(this)
        }
    }
}
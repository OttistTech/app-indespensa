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
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.UiMode
import com.ottistech.indespensa.ui.dialog.IngredientDialogCreator
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.recyclerview.adapter.IngredientAdapter
import com.ottistech.indespensa.ui.recyclerview.helper.ingredientItemTouchHelper
import com.ottistech.indespensa.ui.viewmodel.RecipeFormViewModel
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO

class RecipeFormFragment : Fragment() {

    private lateinit var binding: FragmentRecipeFormBinding
    private lateinit var viewModel: RecipeFormViewModel
    private lateinit var dialogCreator: IngredientDialogCreator
    private lateinit var ingredientsAdapter: IngredientAdapter
    private lateinit var validator : FieldValidations


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeFormBinding.inflate(inflater, container, false)
        viewModel = RecipeFormViewModel(
            RecipeRepository(requireContext())
        )
        dialogCreator = IngredientDialogCreator(requireContext())
        validator = FieldValidations(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupObservers()

        binding.recipeFormImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }

        binding.recipeFormAddIngredientButton.setOnClickListener {
            dialogCreator.showDialog() { ingredient ->
                viewModel.addIngredient(ingredient)
                ingredientsAdapter.addItem(ingredient)
            }
        }

        val levelsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.RECIPE_LEVELS)
        binding.recipeFormSelectLevel.setAdapter(levelsAdapter)

        binding.recipeFormButton.setOnClickListener {
            if(validateForm()) {
                val formRecipe = generateFormRecipe()
                binding.recipeFormButton.isEnabled = false
                viewModel.save(formRecipe, binding.recipeFormImage.drawable.toBitmap())
            }
        }
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUri = result.data?.data
        if(imageUri != null) {
            binding.recipeFormImage.setImageURI(imageUri)
        }
    }

    private fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            when(feedback) {
                UiConstants.ERROR_NOT_FOUND -> showToast("Algum dos ingredientes não foi encontrado!")
                UiConstants.ERROR_BAD_REQUEST -> showToast("Não foi possível criar. Revise as informações!")
                UiConstants.FAIL -> showToast("Não foi possível criar a receita!")
                UiConstants.CREATED -> {
                    showToast(requireContext().getString(R.string.created_successfully, "Item"))
                    findNavController().popBackStack(R.id.recipe_form_dest, true)
                }
            }
            binding.recipeFormButton.isEnabled = true
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

    private fun validateForm() : Boolean {
        with(binding) {
            val isNameValid = validator.validNotNull(recipeFormInputName, recipeFormInputNameContainer, recipeFormInputNameError)
            val isDescriptionValid = validator.validMinLength(recipeFormInputDescription, recipeFormInputDescriptionContainer, recipeFormInputDescriptionError, 12)
            val isTimeValid = validator.validMaxValue(recipeFormInputTime, recipeFormInputTimeContainer, recipeFormInputTimeError, 1440)
            val isLevelValid = validator.validNotNull(recipeFormSelectLevel, recipeFormSelectLevelContainer, recipeFormSelectLevelError)
            val isPreparationMethod = validator.validNotNull(recipeFormPreparationMethodInput, recipeFormPreparationMethodInputContainer, recipeFormPreparationMethodInputError)
                    && validator.validMinLength(recipeFormPreparationMethodInput, recipeFormPreparationMethodInputContainer, recipeFormPreparationMethodInputError, 50)
            val isIngredientsValid = if((viewModel.ingredients.value?.size ?: 0) >= 1) {
                true
            } else {
                validator.setFieldError(null, binding.recipeFormIngredientsError, getString(R.string.form_error_no_ingredients))
                false
            }

            return isNameValid && isDescriptionValid && isTimeValid && isLevelValid && isPreparationMethod && isIngredientsValid
        }
    }

    private fun generateFormRecipe() : RecipeCreateDTO {
        with(binding) {
            return RecipeCreateDTO(
                createdBy=null,
                imageUrl=null,
                title=recipeFormInputName.text.toString(),
                description=recipeFormInputDescription.text.toString(),
                level=RecipeLevel.fromString(recipeFormSelectLevel.text.toString())!!,
                preparationTime=recipeFormInputTime.text.toString().toInt(),
                preparationMethod=recipeFormPreparationMethodInput.text.toString(),
                ingredients=null
            )
        }
    }
}
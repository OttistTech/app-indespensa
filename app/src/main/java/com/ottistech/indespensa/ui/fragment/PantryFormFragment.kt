package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.CategoryRepository
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentPantryFormBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.helpers.DatePickerCreator
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.helpers.toDate
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.activity.MainActivity
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.webclient.dto.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.ProductResponseDTO
import kotlinx.coroutines.launch

class PantryFormFragment : Fragment() {

    private val TAG = "PANTRY FORM FRAGMENT"
    private lateinit var binding : FragmentPantryFormBinding
    private lateinit var validator : FieldValidations
    private lateinit var datePicker : MaterialDatePicker<Long>
    private lateinit var pantryRepository : PantryRepository
    private lateinit var categoryRepository : CategoryRepository


    private var productEanCode : String? = null
    private var productImageUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        validator = FieldValidations(requireContext())
        binding = FragmentPantryFormBinding.inflate(inflater, container, false)
        pantryRepository = PantryRepository()
        categoryRepository = CategoryRepository()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            UiConstants.SCANNER_REQUEST_CODE, this
        ) { _, bundle ->
            val result : ProductResponseDTO? =
                bundle.getSerializable(UiConstants.SCANNER_RESULT_KEY) as ProductResponseDTO?

            result?.let {
                fillForm(it)
                productEanCode = it.eanCode
                productImageUrl = it.imageUrl
            }
        }

        binding.pantryFormImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }

        binding.pantryFormScanButton.setOnClickListener {
            val action = PantryFormFragmentDirections.pantryFormToScanner()
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            val categories = categoryRepository.listCategories()
            val categoriesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
            binding.pantryFormInputCategorySelect.setAdapter(categoriesAdapter)
        }

        val unitiesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.AMOUNT_UNITIES)
        binding.pantryFormInputUnitSelect.setAdapter(unitiesAdapter)

        val datePickerCreator = DatePickerCreator()
        datePicker = datePickerCreator.createDatePicker(binding.pantryFormInputValidityDate, getString(
            com.ottistech.indespensa.R.string.form_hint_validity_date), false)
        binding.pantryFormInputValidityDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE PICKER")
        }

        binding.pantryFormButtonSubmit.setOnClickListener {
            if(validForm()) {
                val newPantryItem = generatePantryItem()
                Log.d(TAG, "Pantry item generated by form: $newPantryItem")
                lifecycleScope.launch {
                    try {
                        Log.d(TAG, "Trying to create pantry item")
                        val result = pantryRepository.createItem(requireContext().getCurrentUser().userId, newPantryItem, binding.pantryFormImage.drawable.toBitmap())
                        if(result) {
                            showToast(requireContext().getString(R.string.created_successfully, "Item"))
                            findNavController().popBackStack(R.id.pantry_form_dest, true)
                        } else {
                            Log.e(TAG, "Failed creating pantry item")
                        }
                    } catch (e: NotFoundException) {
                        Log.e(TAG, "Failed creating pantry item", e)
                    }
                }
            }
        }
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUri = result.data?.data
        if(imageUri != null) {
            binding.pantryFormImage.setImageURI(imageUri)
        }
    }

    private fun generatePantryItem(): PantryItemCreateDTO {
        return PantryItemCreateDTO(
            productEanCode= productEanCode.toString(),
            productName= binding.pantryFormInputName.text.toString(),
            productDescription= binding.pantryFormInputDescription.text.toString(),
            productImageUrl= productImageUrl,
            productAmount= binding.pantryFormInputAmount.text.toString().toDouble(),
            productUnit= binding.pantryFormInputUnitSelect.text.toString(),
            foodName= binding.pantryFormInputFood.text.toString(),
            categoryName= binding.pantryFormInputCategorySelect.text.toString(),
            brandName= binding.pantryFormInputBrand.text.toString(),
            validityDate= binding.pantryFormInputValidityDate.text.toString().toDate()!!
        )
    }

    private fun fillForm(product: ProductResponseDTO) {
        product.imageUrl?.let {
            binding.pantryFormImage.loadImage(it)
            binding.pantryFormImage.tag = it
            binding.pantryFormImage.isEnabled = false
        }
        product.name?.let {
            binding.pantryFormInputName.setText(it)
            binding.pantryFormInputName.isEnabled = false
        }
        product.foodName?.let {
            binding.pantryFormInputFood.setText(it)
            binding.pantryFormInputFood.isEnabled = false
        }
        product.description?.let {
            binding.pantryFormInputDescription.setText(it)
            binding.pantryFormInputDescription.isEnabled = false
        }
        product.categoryName?.let {
            binding.pantryFormInputCategorySelect.setText(it)
            binding.pantryFormInputCategorySelect.isEnabled = false
        }
        product.brandName?.let {
            binding.pantryFormInputBrand.setText(it)
            binding.pantryFormInputBrand.isEnabled = false
        }
        product.amount?.let {
            binding.pantryFormInputAmount.setText(it.toString())
            binding.pantryFormInputAmount.isEnabled = false
        }
        product.unit?.let {
            binding.pantryFormInputUnitSelect.setText(it)
            binding.pantryFormInputUnitSelect.isEnabled = false
        }
    }

    private fun validForm() : Boolean {
        // name
        val isNameValid = validator.validNotNull(binding.pantryFormInputName, binding.pantryFormInputNameContainer, binding.pantryFormInputNameError)
        // food
        val isFoodValid = validator.validNotNull(binding.pantryFormInputFood, binding.pantryFormInputFoodContainer, binding.pantryFormInputFoodError)
        // category
        val isCategoryValid = validator.validNotNull(binding.pantryFormInputCategorySelect, binding.pantryFormInputCategorySelectContainer, binding.pantryFormInputCategorySelectError)
        // description
        val isDescriptionValid = validator.validMinLength(binding.pantryFormInputDescription, binding.pantryFormInputDescriptionContainer, binding.pantryFormInputDescriptionError, 12)
                && validator.validMaxLength(binding.pantryFormInputDescription, binding.pantryFormInputDescriptionContainer, binding.pantryFormInputDescriptionError, 120)
        // brand
        val isBrandValid = validator.validNotNull(binding.pantryFormInputBrand, binding.pantryFormInputBrandContainer, binding.pantryFormInputBrandError)
        // amount
        val isAmountValid = validator.validNotNull(binding.pantryFormInputAmount, binding.pantryFormInputAmountContainer, binding.pantryFormInputAmountError)
        // unity
        val isUnityValid = validator.validNotNull(binding.pantryFormInputUnitSelect, binding.pantryFormInputUnitSelectContainer, binding.pantryFormInputUnitSelectError)
        // validity date
        val isValidityDateValid = validator.validNotNull(binding.pantryFormInputValidityDate, binding.pantryFormInputValidityDateContainer, binding.pantryFormInputValidityDateError)

        return isNameValid && isFoodValid && isCategoryValid && isDescriptionValid &&
                isBrandValid && isAmountValid && isUnityValid && isValidityDateValid
    }
}
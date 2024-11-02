package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.CategoryRepository
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.databinding.FragmentPantryFormBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.dialog.DatePickerCreator
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.ProductSearchAdapter
import com.ottistech.indespensa.ui.viewmodel.PantryFormViewModel
import com.ottistech.indespensa.webclient.dto.product.ProductDTO

class PantryFormFragment : Fragment() {

    private lateinit var binding : FragmentPantryFormBinding
    private lateinit var viewModel : PantryFormViewModel
    private lateinit var searchAdapter: ProductSearchAdapter

    private lateinit var datePicker : MaterialDatePicker<Long>

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val imageUri = result.data?.data
        if(imageUri != null) {
            binding.pantryFormImage.setImageURI(imageUri)
            viewModel.setNewProductImageBitmap(binding.pantryFormImage.drawable.toBitmap())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_pantry_form, container, false)
        viewModel = PantryFormViewModel(
            PantryRepository(requireContext()),
            ProductRepository(requireContext()),
            CategoryRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pantryFormButtonSubmit.setOnClickListener {
            viewModel.submit()
        }

        setupUnitiesSelect(AppConstants.AMOUNT_UNITIES)
        setupDatePicker()
        setupSearchBar()

        setupAdapter()
        setupRecyclerView()

        setupObservers()
        setupValidationListeners()

        binding.pantryFormImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }

        binding.pantryFormScanButton.setOnClickListener {
            val action = PantryFormFragmentDirections.pantryFormToScanner()
            findNavController().navigate(action)
        }

        parentFragmentManager.setFragmentResultListener(
            UiConstants.SCANNER_REQUEST_CODE, this
        ) { _, bundle ->
            val result : ProductDTO? =
                bundle.getSerializable(UiConstants.SCANNER_RESULT_KEY) as ProductDTO?
            result?.let {
                viewModel.updateStateWithProduct(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCategories()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearCallbacks()
    }

    private fun setupAdapter() {
        searchAdapter = ProductSearchAdapter(
            requireContext()
        ) { product ->
            viewModel.fetchProductAndUpdateState(product.productId)
            viewModel.clearCallbacks()
        }
    }

    private fun setupRecyclerView() {
        with(binding.pantryFormProductResultList) {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupDatePicker() {
        val datePickerCreator = DatePickerCreator()
        datePicker = datePickerCreator.createDatePicker(binding.pantryFormInputValidityDate, getString(R.string.form_hint_validity_date), false)
        binding.pantryFormInputValidityDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE PICKER")
        }
    }

    private fun setupUnitiesSelect(data: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        binding.pantryFormInputUnitSelect.setAdapter(adapter)
    }

    private fun setupCategoriesSelect(data: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        binding.pantryFormInputCategorySelect.setAdapter(adapter)
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                setupCategoriesSelect(it)
            }
        }

        viewModel.searchProductResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.pantryFormProductResultList.visibility = View.VISIBLE
                searchAdapter.updateState(it)
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        showToast(feedback.message)
        if(
            feedback.feedbackId == FeedbackId.CREATE_PANTRY_ITEM &&
            feedback.code == FeedbackCode.SUCCESS
        ) {
            popupBackStack()
        }
    }

    private fun popupBackStack() {
        findNavController().popBackStack(R.id.pantry_form_dest, true)
    }

    private fun setupSearchBar() {
        binding.pantryFormSearchbarInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.pantryFormProductResultList.visibility = View.GONE
                    binding.pantryFormBlur.visibility = View.GONE
                    binding.pantryFormSearchbarInput.text = null
                    searchAdapter.clear()
                }, 50)
            } else {
                binding.pantryFormProductResultList.visibility = View.VISIBLE
                binding.pantryFormBlur.visibility = View.VISIBLE
            }
        }
    }

    private fun setupValidationListeners() {
        binding.pantryFormInputName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validProductName() }
        }
        binding.pantryFormInputFood.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validFoodName() }
        }
        binding.pantryFormInputCategorySelect.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validCategory() }
        }
        binding.pantryFormInputDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validDescription() }
        }
        binding.pantryFormInputBrand.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validBrandName() }
        }
        binding.pantryFormInputAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validAmount() }
        }
        binding.pantryFormInputUnitSelect.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validUnit() }
        }
        binding.pantryFormInputValidityDate.addTextChangedListener {
            viewModel.validValidityDate()
        }
    }
}
package com.ottistech.indespensa.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.databinding.DialogIngredientBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.helpers.validNotNull
import com.ottistech.indespensa.ui.recyclerview.adapter.ProductSearchAdapter
import com.ottistech.indespensa.ui.viewmodel.state.IngredientFormErrorState
import com.ottistech.indespensa.ui.viewmodel.state.IngredientFormFieldsState
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IngredientDialogCreator(
    private val context: Context
) {

    private lateinit var dialog: Dialog
    private lateinit var binding: DialogIngredientBinding

    private var searchHandler: Handler? = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val productRepository = ProductRepository()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var searchAdapter: ProductSearchAdapter

    val formState = MutableLiveData(IngredientFormFieldsState())
    val formErrorState = MutableLiveData(IngredientFormErrorState())
    val isFormValid = MutableLiveData(true)

    init {
        formErrorState.observeForever {
            validForm()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showDialog(
        lifecycleOwner: LifecycleOwner,
        onConfirm: (ingredient: RecipeIngredientDTO) -> Unit
    ) {
        setupDialog(lifecycleOwner)
        setupValidationListeners()
        setupProductAdapter()
        setupUnitSelect()
        setupRecyclerView()
        setupCancelButton()
        setupConcludeButton(onConfirm)
        setupSearchBar(
            onResult = { products ->
                binding.dialogIngredientProductsList.visibility = View.VISIBLE
                searchAdapter.updateState(products)
            },
            onResultNotFound = {
                binding.dialogIngredientProductsList.visibility = View.GONE
            },
            onFailure = {
                Toast.makeText(context, "Não foi possível obter os produtos disponíveis", Toast.LENGTH_SHORT).show()
                searchAdapter.clear()
                clearCallbacks()
                dialog.dismiss()
            }
        )

        dialog.show()
    }

    private fun setupConcludeButton(onConfirm: (ingredient: RecipeIngredientDTO) -> Unit) {
        binding.dialogIngredientConcludeButton.setOnClickListener {
            searchAdapter.clear()
            clearCallbacks()
            validAllFields()
            if (isFormValid.value == true) {
                onConfirm(formState.value!!.toRecipeIngredientDTO())
                dialog.dismiss()
            }
        }
    }

    private fun setupCancelButton() {
        binding.dialogIngredientCancelButton.setOnClickListener {
            searchAdapter.clear()
            clearCallbacks()
            dialog.dismiss()
        }
    }

    private fun setupUnitSelect() {
        val unitiesAdapter =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, AppConstants.AMOUNT_UNITIES)
        binding.dialogIngredientInputUnitSelect.setAdapter(unitiesAdapter)
    }

    private fun setupProductAdapter() {
        searchAdapter = ProductSearchAdapter(context) { product ->
            formState.value = formState.value!!.copy(selectedProduct = product)
            formErrorState.value = formErrorState.value!!.copy(selectedProduct = null)
            binding.dialogIngredientSearchField.setText(formState.value!!.selectedProduct!!.productName)
            binding.dialogIngredientProductsList.visibility = View.GONE
            binding.dialogIngredientSearchField.clearFocus()
            clearCallbacks()
        }
    }

    private fun setupDialog(lifecycleOwner: LifecycleOwner) {
        formState.value = IngredientFormFieldsState()
        formErrorState.value = IngredientFormErrorState()

        binding = DialogIngredientBinding.inflate(LayoutInflater.from(context))
        binding.lifecycleOwner = lifecycleOwner
        binding.model = this

        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupSearchBar(
        onResult: (response: List<ProductSearchResponseDTO>) -> Unit,
        onResultNotFound: () -> Unit,
        onFailure: (error: Exception) -> Unit,
    ) {
        binding.dialogIngredientSearchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearCallbacks()
                searchRunnable = Runnable {
                    val query = s.toString()
                    if (query.isNotEmpty()) {
                        coroutineScope.launch {
                            try {
                                val response = productRepository.search(query)
                                onResult(response)
                            } catch (e: ResourceNotFoundException) {
                                onResultNotFound()
                            } catch (e: Exception) {
                                onFailure(e)
                            }
                        }
                    }
                }
                searchHandler?.postDelayed(searchRunnable!!, 500)
            }
        })
        binding.dialogIngredientSearchField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.dialogIngredientProductsList.visibility = View.GONE
                formState.value!!.selectedProduct?.let {
                    binding.dialogIngredientSearchField.setText(it.productName)
                    binding.dialogIngredientSearchField.clearFocus()
                    clearCallbacks()
                }
            } else {
                binding.dialogIngredientProductsList.visibility = View.VISIBLE
            }
        }
    }

    private fun clearCallbacks() {
        searchRunnable?.let {
            searchHandler?.removeCallbacks(it)
        }
    }

    private fun setupRecyclerView() {
        binding.dialogIngredientProductsList.adapter = searchAdapter
        binding.dialogIngredientProductsList.layoutManager = LinearLayoutManager(context)
    }

    private fun validForm() {
        val errorState = formErrorState.value
        isFormValid.value = errorState?.let {
            it.selectedProduct == null &&
                    it.amount == null &&
                    it.unit == null
        } ?: true
    }

    fun validSelectedProduct() {
        val value = formState.value!!.selectedProduct
        val error =
            if( value == null ) { "Selecione um produto!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(selectedProduct=error)
    }

    fun validAmount() {
        val value = formState.value!!.amount
        val error =
            if( !validNotNull(value) ) { "Peso/Volume é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(amount=error)
    }

    fun validUnit() {
        val value = formState.value!!.unit
        val error =
            if( !validNotNull(value) ) { "Unidade é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(unit=error)
    }

    private fun validAllFields() {
        validSelectedProduct()
        validAmount()
        validUnit()
        validForm()
    }

    private fun setupValidationListeners() {
        binding.dialogIngredientAmountField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validAmount()
            }
        }
        binding.dialogIngredientInputUnitSelect.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validUnit()
            }
        }
    }
}
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
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.databinding.DialogIngredientBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.recyclerview.adapter.ProductSearchAdapter
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

    private var selectedProduct: ProductSearchResponseDTO? = null
    private val validator = FieldValidations(context)

    @SuppressLint("ClickableViewAccessibility")
    fun showDialog(
        onConfirm: (ingredient: RecipeIngredientDTO) -> Unit
    ) {
        binding = DialogIngredientBinding.inflate(LayoutInflater.from(context))
        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        searchAdapter = ProductSearchAdapter(context) { product ->
            selectedProduct = product
            validator.removeFieldError(binding.dialogIngredientInputProductSearchContainer, binding.dialogIngredientInputProductSearchError)
            binding.dialogIngredientInputProductSearch.setText(selectedProduct!!.productName)
            binding.dialogIngredientProductsList.visibility = View.GONE
            binding.dialogIngredientInputProductSearch.clearFocus()
            clearCallbacks()
        }

        val unitiesAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, AppConstants.AMOUNT_UNITIES)
        binding.dialogIngredientInputUnitSelect.setAdapter(unitiesAdapter)

        setupRecyclerView(binding.dialogIngredientProductsList)
        dialog.setCanceledOnTouchOutside(false)

        setupSearchBar(
            searchBar = binding.dialogIngredientInputProductSearch,
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

        binding.dialogIngredientCancelButton.setOnClickListener {
            searchAdapter.clear()
            clearCallbacks()
            dialog.dismiss()
        }

        binding.dialogIngredientConcludeButton.setOnClickListener {
            searchAdapter.clear()
            clearCallbacks()
            if(validateForm()) {
                val ingredient = generateFormIngredient()
                onConfirm(ingredient)
                dialog.dismiss()
            }
        }

        binding.dialogIngredientInputProductSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.dialogIngredientProductsList.visibility = View.GONE
                selectedProduct?.let {
                    binding.dialogIngredientInputProductSearch.setText(it.productName)
                    binding.dialogIngredientInputProductSearch.clearFocus()
                    clearCallbacks()
                }
            } else {
                binding.dialogIngredientProductsList.visibility = View.VISIBLE
            }
        }

        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.dialogIngredientInputProductSearch.isFocused) {
                    binding.dialogIngredientInputProductSearch.clearFocus()

                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            false
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun setupSearchBar(
        searchBar: TextInputEditText,
        onResult: (response: List<ProductSearchResponseDTO>) -> Unit,
        onResultNotFound: () -> Unit,
        onFailure: (error: Exception) -> Unit,
    ) {
        searchBar.addTextChangedListener(object : TextWatcher {
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
    }

    private fun clearCallbacks() {
        searchRunnable?.let {
            searchHandler?.removeCallbacks(it)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun validateForm() : Boolean {
        val isAmountValid = validator.validNotNull(binding.dialogIngredientInputAmount, binding.dialogIngredientInputAmountContainer, binding.dialogIngredientInputAmountError)
        val isUnitValid = validator.validNotNull(binding.dialogIngredientInputUnitSelect, binding.dialogIngredientInputUnitSelectContainer, binding.dialogIngredientInputUnitSelectError)
        val isSelectedProductValid = if(selectedProduct != null) {
            true
        } else {
            validator.setFieldError(binding.dialogIngredientInputProductSearchContainer, binding.dialogIngredientInputProductSearchError, context.getString(
                R.string.form_error_not_null, "Produto"))
            false
        }

        return isAmountValid && isUnitValid && isSelectedProductValid
    }

    private fun generateFormIngredient() : RecipeIngredientDTO {
        return RecipeIngredientDTO(
            foodId=selectedProduct!!.foodId,
//            foodName=selectedProduct!!.foodName,
            foodName=selectedProduct!!.foodName,
            amount=binding.dialogIngredientInputAmount.text.toString().toInt(),
            unit= binding.dialogIngredientInputUnitSelect.text.toString(),
            isEssential=binding.dialogIngredientRequiredCheckbox.isChecked
        )
    }
}
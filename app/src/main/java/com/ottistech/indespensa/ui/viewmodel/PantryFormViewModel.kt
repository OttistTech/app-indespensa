package com.ottistech.indespensa.ui.viewmodel

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.CategoryRepository
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.validMaxLength
import com.ottistech.indespensa.ui.helpers.validMinValue
import com.ottistech.indespensa.ui.helpers.validNotNull
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.state.PantryFormActiveState
import com.ottistech.indespensa.ui.viewmodel.state.PantryFormErrorState
import com.ottistech.indespensa.ui.viewmodel.state.PantryFormFieldsState
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import kotlinx.coroutines.launch

class PantryFormViewModel(
    private val pantryRepository: PantryRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var searchHandler: Handler? = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    val searchText: MutableLiveData<String> = MutableLiveData()
    private val _searchProductResult = MutableLiveData<List<ProductSearchResponseDTO>?>()
    val searchProductResult: LiveData<List<ProductSearchResponseDTO>?> = _searchProductResult

    val formState = MutableLiveData(PantryFormFieldsState())
    val formErrorState = MutableLiveData(PantryFormErrorState())
    val formActiveState = MutableLiveData(PantryFormActiveState())

    val isFormValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)


    private val _categories = MutableLiveData<List<String>?>()
    val categories: LiveData<List<String>?> = _categories

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    init {
        searchText.observeForever {
            searchProducts(it)
        }
        formErrorState.observeForever {
            validForm()
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            isLoading.value = true
            _categories.value = categoryRepository.listCategories()
            isLoading.value = false
        }
    }

    private fun searchProducts(query: String) {
        clearCallbacks()
        searchRunnable = Runnable {
            if (query.isNotEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = productRepository.search(query)
                        _searchProductResult.value = response
                    } catch (e: ResourceNotFoundException) {
                        _feedback.value = Feedback(
                            feedbackId= FeedbackId.PRODUCT_SEARCH,
                            code= FeedbackCode.NOT_FOUND,
                            message="Não foram encontrados produtos na busca!"
                        )
                    } catch (e: Exception) {
                        _feedback.value = Feedback(
                            feedbackId= FeedbackId.PRODUCT_SEARCH,
                            code= FeedbackCode.UNHANDLED,
                            message="Não foi possível concluir a busca"
                        )
                    }
                }
            }
        }
        searchHandler?.postDelayed(searchRunnable!!, 500)
    }

    fun clearCallbacks() {
        searchRunnable?.let {
            searchHandler?.removeCallbacks(it)
        }
    }

    fun submit() {
        validAllFields()
        Log.d("AAAAA", formState.value.toString())
        if (isFormValid.value!!) {
            viewModelScope.launch {
                isLoading.value = true
                try {
                    pantryRepository.createItem(
                        formState.value!!.toPantryItemCreateDTO(),
                        formState.value!!.newImageBitmap
                    )
                    _feedback.value = Feedback(
                        feedbackId= FeedbackId.CREATE_PANTRY_ITEM,
                        code= FeedbackCode.SUCCESS,
                        message="Produto adicionado à despensa!"
                    )
                } catch (e: Exception) {
                    _feedback.value = Feedback(
                        feedbackId= FeedbackId.CREATE_PANTRY_ITEM,
                        code= FeedbackCode.UNHANDLED,
                        message="Não foi possível concluir a ação!"
                    )
                }
                isLoading.value = false
            }
        }
    }

    fun updateStateWithProduct(product: ProductDTO) {
        formState.value = PantryFormFieldsState()
        formErrorState.value = PantryFormErrorState()
        formActiveState.value = PantryFormActiveState()
        with(formState.value!!) {
            product.eanCode.let {
                this.productEanCode = it
            }
            product.imageUrl?.let {
                this.imageUrl = it
                formActiveState.value!!.imageUrl = false
            }
            product.name?.let {
                this.productName = it
                formActiveState.value!!.productName = false
            }
            product.foodName?.let {
                this.foodName = it
                formActiveState.value!!.foodName = false
            }
            product.categoryName?.let {
                this.category = it
                formActiveState.value!!.category = false
            }
            product.description?.let {
                this.description = it
                formActiveState.value!!.description = false
            }
            product.brandName?.let {
                this.brandName = it
                formActiveState.value!!.brandName = false
            }
            product.amount?.let {
                this.amount = it.toString()
                formActiveState.value!!.amount = false
            }
            product.unit?.let {
                this.unit = it
                formActiveState.value!!.unit = false
            }
        }

    }

    fun fetchProductAndUpdateState(productId: Long) {
        viewModelScope.launch {
            try {
                val product = productRepository.findById(productId)
                updateStateWithProduct(product)
            } catch (e: Exception) {
                _feedback.value = Feedback(
                    feedbackId= FeedbackId.PRODUCT_DETAILS,
                    code= FeedbackCode.UNHANDLED,
                    message="Não foi possível preencher as informações do produto!"
                )
            }
        }
    }

    private fun validForm() {
        val errorState = formErrorState.value
        isFormValid.value = errorState?.let {
            it.productName == null &&
            it.foodName == null &&
            it.category == null &&
            it.description == null &&
            it.brandName == null &&
            it.amount == null &&
            it.unit == null &&
            it.validityDate == null
        } ?: true
    }

    fun validProductName() {
        val value = formState.value!!.productName
        val error =
            if( !validNotNull(value) ) { "Nome é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(productName=error)
    }

    fun validFoodName() {
        val value = formState.value!!.foodName
        val error =
            if( !validNotNull(value) ) { "Alimento é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(foodName=error)
    }

    fun validCategory() {
        val value = formState.value!!.category
        val error =
            if( !validNotNull(value) ) { "Categoria é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(category=error)
    }

    fun validDescription() {
        val value = formState.value!!.description
        val error =
            if( !validNotNull(value) ) { "Descrição é obrigatório!" }
            else if( !validMaxLength(value, 120) ) { "Descrição deve ter no máximo 120 caracteres!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(description=error)
    }

    fun validBrandName() {
        val value = formState.value!!.brandName
        val error =
            if( !validNotNull(value) ) { "Marca é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(brandName=error)
    }

    fun validAmount() {
        val value = formState.value!!.amount
        val error =
            if( !validNotNull(value) ) { "Peso/volume é obrigatório!" }
            else if( !validMinValue(value, 1) ) { "Peso/volume inválido!" }
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

    fun validValidityDate() {
        val value = formState.value!!.validityDate
        val error =
            if( !validNotNull(value) ) { "Data de validade é obrigatório!" }
            else { null }
        formErrorState.value = formErrorState.value!!.copy(validityDate=error)
    }

    private fun validAllFields() {
        validProductName()
        validFoodName()
        validCategory()
        validDescription()
        validBrandName()
        validAmount()
        validUnit()
        validValidityDate()
        validForm()
    }

    fun setNewProductImageBitmap(bitmap: Bitmap) {
        formState.value!!.newImageBitmap = bitmap
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            url?.let {
                view.loadImage(it)
            }
        }
    }

}
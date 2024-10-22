package com.ottistech.indespensa.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import kotlinx.coroutines.launch

class ShopViewModel(
    private val repository: ShopRepository,
    private val pantryRepository: PantryRepository,
    private val productRepository: ProductRepository,
    private val shopRepository: ShopRepository
) : ViewModel() {

    private var searchHandler: Handler? = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val _shopState = MutableLiveData<List<ShopItemPartialDTO>?>()
    val shopState: LiveData<List<ShopItemPartialDTO>?> = _shopState

    private val _searchProductResult = MutableLiveData<List<ProductSearchResponseDTO>?>()
    val searchProductResult: LiveData<List<ProductSearchResponseDTO>?> = _searchProductResult

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    private val shopChanges = mutableListOf<ProductItemUpdateAmountDTO>()

    fun fetchShop() {
        viewModelScope.launch {
            try {
                val shopItems = repository.listItems()
                _shopState.value = shopItems
            } catch(e: ResourceNotFoundException) {
                _feedback.value = Feedback(
                    feedbackId=FeedbackId.SHOPLIST,
                    code=FeedbackCode.NOT_FOUND,
                    message="Sua lista de compras está vazia no momento!"
                )
            }
        }
    }

    fun syncChanges() {
        viewModelScope.launch {
            repository.updateItemsAmount(*shopChanges.toTypedArray())
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        val change = ProductItemUpdateAmountDTO(itemId, amount)
        shopChanges.add(change)
    }

    fun addAllItemsFromShopToPantry() {
        viewModelScope.launch {
            try {
                pantryRepository.addAllShopItemsToPantry()
                _feedback.value = Feedback(
                    feedbackId=FeedbackId.ADD_ALL_TO_PANTRY,
                    code=FeedbackCode.SUCCESS,
                    message="Produtos adicionados à despensa!"
                )
            } catch (e: Exception) {
                _feedback.value = Feedback(
                    feedbackId=FeedbackId.ADD_ALL_TO_PANTRY,
                    code=FeedbackCode.UNHANDLED,
                    message="Não foi possível concluir a ação!"
                )
            }
        }
    }

    fun searchProducts(query: String) {
        clearCallbacks()
        searchRunnable = Runnable {
            if (query.isNotEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = productRepository.search(query)
                        _searchProductResult.value = response
                    } catch (e: ResourceNotFoundException) {
                        _feedback.value = Feedback(
                            feedbackId=FeedbackId.PRODUCT_SEARCH,
                            code=FeedbackCode.NOT_FOUND,
                            message="Não foram encontrados produtos na busca!"
                        )
                    } catch (e: Exception) {
                        _feedback.value = Feedback(
                            feedbackId=FeedbackId.PRODUCT_SEARCH,
                            code=FeedbackCode.UNHANDLED,
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

    fun addItemToShopList(productId: Long) {
        viewModelScope.launch {
            try {
                shopRepository.addItem(productId)
                _feedback.value = Feedback(
                    feedbackId=FeedbackId.ADD_TO_SHOPLIST,
                    code=FeedbackCode.SUCCESS,
                    message="Produto adicionado à lista de compras!"
                )
                fetchShop()
            } catch (e: Exception) {
                _feedback.value = Feedback(
                    feedbackId=FeedbackId.ADD_TO_SHOPLIST,
                    code=FeedbackCode.UNHANDLED,
                    message="Não foi possível adicionar o produto!"
                )
            }
        }
    }

}
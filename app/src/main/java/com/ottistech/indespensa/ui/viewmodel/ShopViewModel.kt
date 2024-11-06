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
import com.ottistech.indespensa.data.repository.RecommendationRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import kotlinx.coroutines.launch

class ShopViewModel(
    private val shopRepository: ShopRepository,
    private val pantryRepository: PantryRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    private var searchHandler: Handler? = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    val isLoading = MutableLiveData(false)

    private val _shopItems = MutableLiveData<List<ShopItemPartialDTO>?>()
    val shopItems: LiveData<List<ShopItemPartialDTO>?> = _shopItems

    private val _recommendations = MutableLiveData<List<ProductDTO>?>()
    val recommendations: LiveData<List<ProductDTO>?> = _recommendations

    private val _searchResult = MutableLiveData<List<ProductSearchResponseDTO>?>()
    val searchResult: LiveData<List<ProductSearchResponseDTO>?> = _searchResult

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    private val shopChanges = mutableListOf<ProductItemUpdateAmountDTO>()

    fun fetchShop() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val shopItems = shopRepository.list()
                _shopItems.value = shopItems
            } catch(e: ResourceNotFoundException) {
                _feedback.value =
                    Feedback(FeedbackId.SHOPLIST, FeedbackCode.NOT_FOUND, "Sua lista de compras está vazia no momento!")
                fetchRecommendations()
            } catch(e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.SHOPLIST, FeedbackCode.UNHANDLED, "Não foi possível carregar sua lista de compras!")
            }
            isLoading.value = false
        }
    }

    suspend fun fetchRecommendations() {
        try {
            val recommendations = recommendationRepository.getProductRecommendations()
            _recommendations.value = recommendations
        } catch(e: ResourceNotFoundException) {
            _feedback.value =
                Feedback(FeedbackId.RECOMMENDATIONS, FeedbackCode.NOT_FOUND, "Sua lista de compras está vazia no momento!")
        }
    }

    fun syncChanges() {
        viewModelScope.launch {
            shopRepository.updateItemsAmount(*shopChanges.toTypedArray())
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        val existingItemIndex = shopChanges.indexOfFirst { it.itemId == itemId }
        if (existingItemIndex != -1) {
            shopChanges[existingItemIndex].amount = amount
        } else {
            val change = ProductItemUpdateAmountDTO(itemId, amount)
            shopChanges.add(change)
        }
    }

    fun addAllItemsFromShopToPantry() {
        viewModelScope.launch {
            try {
                pantryRepository.addAllShopItems()
                _feedback.value =
                    Feedback(FeedbackId.ADD_ALL_TO_PANTRY, FeedbackCode.SUCCESS, "Produtos adicionados à despensa!")
                fetchShop()
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.ADD_ALL_TO_PANTRY, FeedbackCode.UNHANDLED, "Não foi possível concluir a ação!")
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
                        _searchResult.value = response
                    } catch (e: ResourceNotFoundException) {
                        _feedback.value =
                            Feedback(FeedbackId.PRODUCT_SEARCH, FeedbackCode.NOT_FOUND, "Não foram encontrados produtos na busca!")
                    } catch (e: Exception) {
                        _feedback.value =
                            Feedback(FeedbackId.PRODUCT_SEARCH, FeedbackCode.UNHANDLED, "Não foi possível concluir a busca")
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
                shopRepository.add(productId)
                _feedback.value =
                    Feedback(FeedbackId.ADD_TO_SHOPLIST, FeedbackCode.SUCCESS, "Produto adicionado à lista de compras!")
                fetchShop()
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.ADD_TO_SHOPLIST, FeedbackCode.UNHANDLED, "Não foi possível adicionar o produto!")
            }
        }
    }

}
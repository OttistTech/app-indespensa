package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import kotlinx.coroutines.launch

class ShopViewModel(
    private val repository: ShopRepository
) : ViewModel() {

    private val _shopState = MutableLiveData<List<ShopItemPartialDTO>?>()
    val shopState: LiveData<List<ShopItemPartialDTO>?> = _shopState

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    private val shopChanges = mutableListOf<ProductItemUpdateAmountDTO>()

    fun fetchShop() {
        viewModelScope.launch {
            try {
                val shopItems = repository.listItems()
                _shopState.value = shopItems
                _error.value = null
            } catch(e: ResourceNotFoundException) {
                _error.value = UiConstants.ERROR_NOT_FOUND
            }
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        val change = ProductItemUpdateAmountDTO(itemId, amount)
        shopChanges.add(change)
    }

    fun syncChanges() {
        viewModelScope.launch {
            repository.updateItemsAmount(*shopChanges.toTypedArray())
        }
    }


}
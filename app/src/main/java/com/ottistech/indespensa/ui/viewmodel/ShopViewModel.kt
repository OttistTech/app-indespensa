package com.ottistech.indespensa.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO
import kotlinx.coroutines.launch

class ShopViewModel(
    private val repository: ShopRepository,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val TAG = "SHOP VIEWMODEL"

    private val _shopState = MutableLiveData<List<ShopItemPartialDTO>?>()
    val shopState: LiveData<List<ShopItemPartialDTO>?> = _shopState

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    private val _message = MutableLiveData<Int?>()
    val message: LiveData<Int?> = _message

    private val shopChanges = mutableListOf<ProductItemUpdateAmountDTO>()

    fun fetchShop() {
        Log.d(TAG, "[fetchShop] Requesting shop items information")

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

    fun syncChanges() {
        Log.d(TAG, "[syncChanges] Requesting for changed shop items synchronization")

        viewModelScope.launch {
            repository.updateItemsAmount(*shopChanges.toTypedArray())
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        Log.d(TAG, "[registerItemChange] Shop item $itemId amount was changed to $amount")

        val change = ProductItemUpdateAmountDTO(itemId, amount)
        shopChanges.add(change)
    }

    fun addAllItemsFromShopToPantry() {
        Log.d(TAG, "[addAllItemsFromShopToPantry] Requesting to add all shop items to pantry")

        viewModelScope.launch {
            try {
                pantryRepository.addAllShopItemsToPantry()
                _message.value = UiConstants.OK
            } catch (e: Exception) {
                _message.value = UiConstants.FAIL
                Log.e(TAG, "[addAllItemsFromShopToPantry] Failed to add items: ${e.message}")
            }
        }
    }

}
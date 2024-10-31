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
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import kotlinx.coroutines.launch
import java.util.Date

class ShopItemDetailsViewModel(
    private val shopRepository: ShopRepository,
    private val pantryRepository: PantryRepository
) : ViewModel(), ProductItemDetailsViewModel {

    private val TAG = "SHOP ITEM DETAILS VIEWMODEL"

    private val _itemDetails = MutableLiveData<ShopItemDetailsDTO?>()
    override val itemDetails: MutableLiveData<ShopItemDetailsDTO?> = _itemDetails

    private val _message = MutableLiveData<Int?>()
    override val message: LiveData<Int?> = _message

    private val _itemAmount = MutableLiveData<Int>()
    override val itemAmount: LiveData<Int> = _itemAmount

    override fun getItemDetails(itemId: Long) {
        Log.d(TAG, "[getItemDetails] Requesting item $itemId details")
        viewModelScope.launch {
            try {
                val itemDetails = shopRepository.getItemDetails(itemId)
                if(itemDetails != null) {
                    _itemDetails.value = itemDetails
                    _itemAmount.value = itemDetails.amount
                } else {
                    _message.value = UiConstants.ERROR_NOT_FOUND
                }
            } catch(e: ResourceNotFoundException) {
                _message.value = UiConstants.ERROR_NOT_FOUND
            }
        }
    }

    override fun syncChanges() {
        Log.d(TAG, "[syncChanges] Requesting for changed item synchronization")
        val shopItemId: Long? = _itemDetails.value?.itemId
        val newAmount: Int? = _itemAmount.value
        if(shopItemId != null && newAmount != null) {
            viewModelScope.launch {
                shopRepository.updateItemsAmount(
                    ProductItemUpdateAmountDTO(
                        shopItemId,
                        newAmount
                    )
                )
            }
        }
    }

    override fun registerAmountChange(amountChange: Int) {
        Log.d(TAG, "[registerItemChange] Shop item amount was changed in $amountChange")
        _itemAmount.value?.plus(amountChange)?.let { newAmount ->
            if(newAmount >= 0) {
                _itemAmount.value = newAmount
            }
        }
    }

    fun addToPantry(validityDate: Date) {
        Log.d(TAG, "[addToPantry] Requesting for adding item to pantry")
        viewModelScope.launch {
            Log.d("ITEMMMMM", _itemDetails.value.toString())
            _itemDetails.value?.let { item ->
                Log.d("ITEMMMMM", "here")
                syncChanges()
                val result = pantryRepository.addItem(item.itemId, validityDate)
                if(result != null) {
                    _message.value = UiConstants.OK
                } else {
                    _message.value = UiConstants.FAIL
                }
            }
        }
    }

}
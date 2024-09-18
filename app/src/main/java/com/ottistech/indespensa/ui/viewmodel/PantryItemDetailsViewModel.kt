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
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemUpdateAmountDTO
import kotlinx.coroutines.launch

class PantryItemDetailsViewModel(
    private val pantryRepository: PantryRepository,
    private val shopRepository: ShopRepository
) : ViewModel(), ProductItemDetailsViewModel {

    private val TAG = "PANTRY ITEM DETAILS VIEWMODEL"

    private val _itemDetails = MutableLiveData<PantryItemDetailsDTO>()
    override val itemDetails: LiveData<PantryItemDetailsDTO> = _itemDetails

    private val _message = MutableLiveData<Int?>()
    override val message: LiveData<Int?> = _message

    private val _itemAmount = MutableLiveData<Int>()
    override val itemAmount: LiveData<Int> = _itemAmount

    override fun getItemDetails(itemId: Long) {
        Log.d(TAG, "[getItemDetails] Requesting item $itemId details")
        viewModelScope.launch {
            try {
                val itemDetails = pantryRepository.getItemDetails(itemId)
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
        val pantryItemId: Long? = _itemDetails.value?.itemId
        val newAmount: Int? = _itemAmount.value
        if(pantryItemId != null && newAmount != null) {
            viewModelScope.launch {
                pantryRepository.updateItemsAmount(
                    PantryItemUpdateAmountDTO(
                        pantryItemId,
                        newAmount
                    )
                )
            }
        }
    }

    override fun registerAmountChange(amountChange: Int) {
        Log.d(TAG, "[registerItemChange] Pantry item amount was changed in $amountChange")
        _itemAmount.value?.plus(amountChange)?.let { newAmount ->
            if(newAmount >= 0) {
                _itemAmount.value = newAmount
            }
        }
    }

    fun addToShopList() {
        Log.d(TAG, "[addToShopList] Requesting for adding item to shop list")
        val productId: Long? = _itemDetails.value?.productId
        if(productId != null) {
            viewModelScope.launch {
                val result = shopRepository.addItem(productId)
                if(result) {
                    _message.value = UiConstants.OK
                } else {
                    _message.value = UiConstants.FAIL
                }
            }
        }
    }
}
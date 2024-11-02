package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import kotlinx.coroutines.launch

class PantryItemDetailsViewModel(
    private val pantryRepository: PantryRepository,
    private val shopRepository: ShopRepository,
) : ViewModel(), ProductItemDetailsViewModel {

    private val _itemDetails = MutableLiveData<PantryItemDetailsDTO?>()
    override val itemDetails: MutableLiveData<PantryItemDetailsDTO?> = _itemDetails

    private val _feedback = MutableLiveData<Feedback?>()
    override val feedback: LiveData<Feedback?> = _feedback

    private val _itemAmount = MutableLiveData<Int>()
    override val itemAmount: LiveData<Int> = _itemAmount

    override fun getItemDetails(itemId: Long) {
        viewModelScope.launch {
            try {
                val itemDetails = pantryRepository.getItemDetails(itemId)
                if(itemDetails != null) {
                    _itemDetails.value = itemDetails
                    _itemAmount.value = itemDetails.amount
                } else {
                    _feedback.value =
                        Feedback(FeedbackId.GET_ITEM_DETAILS, FeedbackCode.UNHANDLED, "Não foi possível carregar as informações")
                }
            } catch(e: ResourceNotFoundException) {
                _feedback.value =
                    Feedback(FeedbackId.GET_ITEM_DETAILS, FeedbackCode.NOT_FOUND, "Informações não encontradas")
            }
        }
    }

    override fun syncChanges() {
        val pantryItemId: Long? = _itemDetails.value?.itemId
        val newAmount: Int? = _itemAmount.value
        if(pantryItemId != null && newAmount != null) {
            viewModelScope.launch {
                pantryRepository.updateItemsAmount(
                    ProductItemUpdateAmountDTO(
                        pantryItemId,
                        newAmount
                    )
                )
            }
        }
    }

    override fun registerAmountChange(amountChange: Int) {
        _itemAmount.value?.plus(amountChange)?.let { newAmount ->
            if(newAmount >= 0) {
                _itemAmount.value = newAmount
            }
        }
    }

    fun addToShopList() {
        val productId: Long? = _itemDetails.value?.productId
        if(productId != null) {
            viewModelScope.launch {
                syncChanges()
                try {
                    shopRepository.addItem(productId)
                    _feedback.value =
                        Feedback(FeedbackId.ADD_TO_SHOPLIST, FeedbackCode.SUCCESS, "Adicionado à lista de compras!")
                } catch (e: Exception) {
                    _feedback.value =
                        Feedback(FeedbackId.ADD_TO_SHOPLIST, FeedbackCode.UNHANDLED, "Não foi possível adicionar!")
                }
            }
        }
    }
}
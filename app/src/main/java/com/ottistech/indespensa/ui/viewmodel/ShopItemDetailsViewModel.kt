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
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import kotlinx.coroutines.launch
import java.util.Date

class ShopItemDetailsViewModel(
    private val shopRepository: ShopRepository,
    private val pantryRepository: PantryRepository
) : ViewModel(), ProductItemDetailsViewModel {

    private val _itemDetails = MutableLiveData<ShopItemDetailsDTO?>()
    override val itemDetails: MutableLiveData<ShopItemDetailsDTO?> = _itemDetails

    private val _feedback = MutableLiveData<Feedback?>()
    override val feedback: LiveData<Feedback?> = _feedback

    private val _itemAmount = MutableLiveData<Int>()
    override val itemAmount: LiveData<Int> = _itemAmount

    override fun getItemDetails(itemId: Long) {
        viewModelScope.launch {
            try {
                val itemDetails = shopRepository.getDetails(itemId)
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
            } catch (e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.GET_ITEM_DETAILS, FeedbackCode.UNHANDLED, "Não foi possível carregar!")
            }
        }
    }

    override fun syncChanges() {
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
        _itemAmount.value?.plus(amountChange)?.let { newAmount ->
            if(newAmount >= 0) {
                _itemAmount.value = newAmount
            }
        }
    }

    fun addToPantry(validityDate: Date) {
        viewModelScope.launch {
            _itemDetails.value?.let { item ->
                syncChanges()
                try {
                    pantryRepository.addShopItem(item.itemId, validityDate)
                    _feedback.value =
                        Feedback(FeedbackId.ADD_TO_PANTRY, FeedbackCode.SUCCESS, "Adicionado à despensa!")
                } catch (e: Exception) {
                    _feedback.value =
                        Feedback(FeedbackId.ADD_TO_PANTRY, FeedbackCode.UNHANDLED, "Não foi possível adicionar!")
                }
            }
        }
    }

}
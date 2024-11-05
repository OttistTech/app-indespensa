package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import kotlinx.coroutines.launch

class PantryViewModel(
    private val repository: PantryRepository
) : ViewModel() {

    private val _pantryState = MutableLiveData<List<PantryItemPartialDTO>?>()
    val pantryState: LiveData<List<PantryItemPartialDTO>?> = _pantryState

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    private val pantryChanges = mutableListOf<ProductItemUpdateAmountDTO>()

    fun fetchPantry() {
        viewModelScope.launch {
            try {
                val pantryItems = repository.list()
                _pantryState.value = pantryItems
            } catch(e: ResourceNotFoundException) {
                _feedback.value =
                    Feedback(FeedbackId.PANTRY_LIST, FeedbackCode.NOT_FOUND, "Sua despensa está vazia no momento.")
            } catch(e: Exception) {
                _feedback.value =
                    Feedback(FeedbackId.PANTRY_LIST, FeedbackCode.UNHANDLED, "Não foi possível carregar a despensa!")
            }
        }
    }

    fun syncChanges() {
        if(pantryChanges.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateAmount(*pantryChanges.toTypedArray())
            }
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        val existingItemIndex = pantryChanges.indexOfFirst { it.itemId == itemId }
        if (existingItemIndex != -1) {
            pantryChanges[existingItemIndex].amount = amount
        } else {
            val change = ProductItemUpdateAmountDTO(itemId, amount)
            pantryChanges.add(change)
        }
    }
}
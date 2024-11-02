package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import kotlinx.coroutines.launch

class ShopHistoryViewModel(
    private val repository: ShopRepository
) : ViewModel() {

    private val _history = MutableLiveData<List<PurchaseDTO>?>()
    val history: LiveData<List<PurchaseDTO>?> = _history

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    fun fetchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _history.value = repository.getHistory()
            } catch(e: ResourceNotFoundException) {
                _feedback.value =
                    Feedback(FeedbackId.SHOP_HISTORY, FeedbackCode.NOT_FOUND, "")
            }
            _isLoading.value = false
        }
    }
}
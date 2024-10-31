package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO
import kotlinx.coroutines.launch

class ShopHistoryViewModel(
    private val repository: ShopRepository
) : ViewModel() {

    private val _history = MutableLiveData<List<PurchaseDTO>?>()
    val history: LiveData<List<PurchaseDTO>?> = _history

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    fun fetchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _history.value = repository.getHistory()
                _error.value = null
            } catch(e: ResourceNotFoundException) {
                _error.value = UiConstants.ERROR_NOT_FOUND
            }
            _isLoading.value = false
        }
    }
}
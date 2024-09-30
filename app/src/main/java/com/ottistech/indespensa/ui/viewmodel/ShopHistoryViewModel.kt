package com.ottistech.indespensa.ui.viewmodel

import android.util.Log
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

    private val TAG = "SHOP HISTORY VIEWMODEL"

    private val _history = MutableLiveData<List<PurchaseDTO>?>()
    val history: LiveData<List<PurchaseDTO>?> = _history

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    fun fetchHistory() {
        Log.d(TAG, "[fetchHistory] Requesting history")
        viewModelScope.launch {
            try {
                _history.value = repository.getHistory()
                _error.value = null
            } catch(e: ResourceNotFoundException) {
                _error.value = UiConstants.ERROR_NOT_FOUND
            }
        }
    }
}
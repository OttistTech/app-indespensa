package com.ottistech.indespensa.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.PantryItemUpdateDTO
import kotlinx.coroutines.launch

class PantryViewModel(
    private val repository: PantryRepository
) : ViewModel() {

    private val TAG = "UPDATE USER VIEWMODEL"

    private val _pantryState = MutableLiveData<List<PantryItemPartialDTO>?>()
    val pantryState: LiveData<List<PantryItemPartialDTO>?> = _pantryState

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    private val pantryChanges = mutableListOf<PantryItemUpdateDTO>()

    fun fetchPantry() {
        Log.d(TAG, "[fetchPantry] Requesting pantry items information")
        viewModelScope.launch {
            try {
                val pantryItems = repository.listItems()
                _pantryState.value = pantryItems
                _error.value = null
            } catch(e: ResourceNotFoundException) {
                _error.value = UiConstants.ERROR_NOT_FOUND
            }
        }
    }

    fun syncChanges() {
        Log.d(TAG, "[syncChanges] Requesting for changed pantry items synchronization")
        viewModelScope.launch {
            repository.updateItemsAmount(pantryChanges)
        }
    }

    fun registerItemChange(itemId: Long, amount: Int) {
        Log.d(TAG, "[registerItemChange] Pantry item $itemId amount was changed to $amount")
        val change = PantryItemUpdateDTO(itemId, amount)
        pantryChanges.add(change)
    }
}
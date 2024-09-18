package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData

// TODO: Future convert this interface into a Parent Class extended by ProductItemDetailsViewModel's maybe
interface ProductItemDetailsViewModel {
    val itemDetails: LiveData<*>
    val message: LiveData<Int?>
    val itemAmount: LiveData<Int>

    fun getItemDetails(itemId: Long)
    fun syncChanges()
    fun registerAmountChange(amountChange: Int)
}
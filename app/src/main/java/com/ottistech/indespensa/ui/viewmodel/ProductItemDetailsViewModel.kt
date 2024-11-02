package com.ottistech.indespensa.ui.viewmodel

import androidx.lifecycle.LiveData
import com.ottistech.indespensa.ui.model.feedback.Feedback

interface ProductItemDetailsViewModel {
    val itemDetails: LiveData<*>
    val feedback: LiveData<Feedback?>
    val itemAmount: LiveData<Int>


    fun getItemDetails(itemId: Long)
    fun syncChanges()
    fun registerAmountChange(amountChange: Int)
}
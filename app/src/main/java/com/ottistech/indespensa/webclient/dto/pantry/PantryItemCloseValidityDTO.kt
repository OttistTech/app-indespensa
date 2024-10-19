package com.ottistech.indespensa.webclient.dto.pantry

import java.util.Date

data class PantryItemCloseValidityDTO (
    val pantryItemId: Long,
    val productName: String,
    val amount: Double,
    val unit: String,
    val validityDate: Date
)
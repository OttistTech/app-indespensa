package com.ottistech.indespensa.webclient.dto.pantry

import java.util.Date

data class PantryItemPartialDTO (
    val userId: Long,
    val pantryItemId: Long,
    val name: String,
    val imageUrl: String?,
    val productAmount: Double,
    val productUnit: String,
    var pantryAmount: Int,
    val validityDate: Date
)
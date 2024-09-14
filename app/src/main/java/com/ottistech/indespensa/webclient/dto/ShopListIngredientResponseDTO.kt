package com.ottistech.indespensa.webclient.dto

data class ShopListIngredientResponseDTO(
    val listItemId: Long,
    val userId: Long,
    val productName: String,
    val imageUrl: String,
    val amount: Int,
    val productAmount: Double,
    val productUnit: String
)
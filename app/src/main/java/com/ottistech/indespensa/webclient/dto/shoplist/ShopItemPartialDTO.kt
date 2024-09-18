package com.ottistech.indespensa.webclient.dto.shoplist

data class ShopItemPartialDTO (
    val listItemId: Long,
    val userId: Long,
    val productName: String,
    val imageUrl: String,
    val amount: Int,
    val productAmount: Double,
    val productUnit: String
)

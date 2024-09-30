package com.ottistech.indespensa.webclient.dto.shoplist

import java.util.Date

data class PurchaseDTO (
    val purchaseDate: Date,
    val dailyAmount: Int,
    val historyDataItems: List<PurchaseItemDTO>
)

data class PurchaseItemDTO (
    val productId: Long,
    val productName: String,
    val imageUrl: String,
    val amount: Int
)
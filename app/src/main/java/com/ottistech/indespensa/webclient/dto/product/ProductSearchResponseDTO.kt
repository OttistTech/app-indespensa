package com.ottistech.indespensa.webclient.dto.product

data class ProductSearchResponseDTO (
    val productId: Long,
    val foodId: Long,
    val foodName: String,
    val productName: String,
    val imageUrl: String
)

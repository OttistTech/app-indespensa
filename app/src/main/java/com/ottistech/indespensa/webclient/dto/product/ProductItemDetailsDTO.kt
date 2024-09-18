package com.ottistech.indespensa.webclient.dto.product

import java.math.BigDecimal

interface ProductItemDetailsDTO {
    val itemId: Long
    val userId: Long
    val productId: Long
    val productImageUrl: String
    val productFood: String
    val productName: String
    val productBrand: String
    val productDescription: String
    val productAmount: BigDecimal
    val productUnit: String
    val amount: Int
}
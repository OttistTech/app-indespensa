package com.ottistech.indespensa.webclient.dto

import java.util.Date

data class PantryItemCreateDTO (
    val productEanCode: String?,
    val productName: String,
    val productDescription: String,
    var productImageUrl: String?,
    val productAmount: Double,
    val productUnit: String,
    val foodName: String,
    val categoryName: String,
    val brandName: String,
    val pantryAmount: Int = 1,
    val validityDate: Date
)
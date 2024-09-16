package com.ottistech.indespensa.webclient.dto

import java.util.Date

data class PantryItemDTO (
    val productEanCode: String?,
    val productName: String,
    val productImageUrl: String?,
    val foodName: String,
    val categoryName: String,
    val productDescription: String,
    val brandName: String,
    val productAmount: Double,
    val productUnit: String,
    val productType: String?,
    val userId: String,
    val pantryItemAmount: Int,
    val pantryItemValidityDate: Date,
    val pantryItemPurchaseDate: Date?,
    val pantryItemIsActive: Boolean
)
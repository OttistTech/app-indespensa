package com.ottistech.indespensa.webclient.dto

import java.io.Serializable

data class ProductResponseDTO (
    val eanCode: String,
    val name: String?,
    val imageUrl: String?,
    val foodName: String?,
    val categoryName: String?,
    val description: String?,
    val brandName: String?,
    val amount: Double?,
    val unit: String?,
    val type: String?
) : Serializable
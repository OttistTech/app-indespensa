package com.ottistech.indespensa.webclient.dto.recipe

import java.math.BigDecimal

data class RecipeIngredientDetailsDTO(
    val foodId: Long,
    val  foodName: String,
    val amount: BigDecimal,
    val unit: String,
    val isEssential: Boolean,
    val isInPantry: Boolean
)

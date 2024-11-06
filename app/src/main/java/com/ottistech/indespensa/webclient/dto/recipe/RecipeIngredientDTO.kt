package com.ottistech.indespensa.webclient.dto.recipe

data class RecipeIngredientDTO (
    val foodId: Long,
    val foodName: String,
    val amount: Int,
    val unit: String,
    val isEssential: Boolean,
    val isInPantry: Boolean = false
)
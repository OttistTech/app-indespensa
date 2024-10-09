package com.ottistech.indespensa.webclient.dto.recipe

data class RecipeIngredientCreateDTO (
    val foodId: Long,
    val amount: Int,
    val unit: String,
    val isEssential: Boolean
)


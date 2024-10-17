package com.ottistech.indespensa.webclient.dto.recipe

import com.ottistech.indespensa.shared.RecipeLevel

data class RecipeFullDTO (
    val recipeId: Long,
    val createdBy: Long,
    val imageUrl: String?,
    val title: String,
    val description: String,
    val level: RecipeLevel,
    val numStars: Float,
    val preparationTime: Int,
    val amountInPantry: Int,
    val amountIngredients: Int,
    val preparationMethod: String,
    val isShared: Boolean = false,
    val ingredients: List<RecipeIngredientDTO>
)
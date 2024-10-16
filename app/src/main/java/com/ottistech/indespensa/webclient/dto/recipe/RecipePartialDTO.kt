package com.ottistech.indespensa.webclient.dto.recipe

import com.ottistech.indespensa.shared.RecipeLevel

data class RecipePartialDTO(
    val recipeId: Long,
    val imageUrl: String,
    val title: String,
    val description: String,
    val amountIngredients: Int,
    val amountInPantry: Int,
    val level: RecipeLevel,
    val preparationTime: Int,
    val numStars: Float
)

package com.ottistech.indespensa.webclient.dto.recipe

data class RecipeDetailsDTO(
    val recipeId: Long,
    val imageUrl: String,
    val level: String,
    val title: String,
    val numStars: Int,
    val description: String,
    val amountIngredients: Int,
    val amountInPantry: Int,
    val preparationTime: Int,
    val preparationMethod: String,
    val ingredients: List<RecipeIngredientDetailsDTO>
)

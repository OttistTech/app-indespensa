package com.ottistech.indespensa.webclient.dto.recipe

import com.google.gson.annotations.SerializedName
import com.ottistech.indespensa.shared.RecipeLevel

data class RecipeCreateDTO (
    var createdBy: Long?,
    var imageUrl: String?,
    val title: String,
    val description: String,
    val level: RecipeLevel,
    val preparationTime: Int,
    val preparationMethod: String,
    var isShared: Boolean = false,
    @SerializedName("createRecipeIngredientList")
    var ingredients: List<RecipeIngredientCreateDTO>?
)


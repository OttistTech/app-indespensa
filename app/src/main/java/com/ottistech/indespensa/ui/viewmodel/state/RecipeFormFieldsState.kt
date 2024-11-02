package com.ottistech.indespensa.ui.viewmodel.state

import android.graphics.Bitmap
import com.ottistech.indespensa.shared.RecipeLevel
import com.ottistech.indespensa.webclient.dto.recipe.RecipeCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientCreateDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO

data class RecipeFormFieldsState (
    var newImageBitmap: Bitmap? = null,
    var name: String? = null,
    var description: String? = null,
    var time: String? = null,
    var level: String? = null,
    var ingredients: List<RecipeIngredientDTO>? = emptyList(),
    var preparationMethod: String? = null
) {

    fun toRecipeCreateDTO(): RecipeCreateDTO {
        return RecipeCreateDTO(
            title=name ?: "",
            description=description ?: "",
            preparationTime=time?.toInt() ?: 0,
            level=RecipeLevel.fromString(level) ?: RecipeLevel.EASY,
            ingredients=ingredients?.map {
                RecipeIngredientCreateDTO(it.foodId, it.amount, it.unit, it.isEssential)
            },
            preparationMethod=preparationMethod ?: ""
        )
    }
}

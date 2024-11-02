package com.ottistech.indespensa.ui.viewmodel.state

import androidx.lifecycle.MutableLiveData
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO
import com.ottistech.indespensa.webclient.dto.recipe.RecipeIngredientDTO

data class IngredientFormFieldsState (
    var selectedProduct: ProductSearchResponseDTO? = null,
    var amount: String? = null,
    var unit: String? = null,
    var isRequired: MutableLiveData<Boolean> = MutableLiveData(false)
) {

    fun toRecipeIngredientDTO() : RecipeIngredientDTO {
        return RecipeIngredientDTO(
            foodId=selectedProduct!!.foodId,
            foodName=selectedProduct!!.foodName,
            amount=amount?.toInt() ?: 0,
            unit=unit ?: "",
            isEssential=isRequired.value ?: false
        )
    }

}

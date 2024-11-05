package com.ottistech.indespensa.ui.viewmodel.state

import android.graphics.Bitmap
import com.ottistech.indespensa.shared.toDate
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import java.util.Date

data class PantryFormFieldsState (
    var productEanCode: String? = null,
    var newImageBitmap: Bitmap? = null,
    var imageUrl: String? = null,
    var productName: String? = null,
    var foodName: String? = null,
    var category: String? = null,
    var description: String? = null,
    var brandName: String? = null,
    var amount: String? = null,
    var unit: String? = null,
    var validityDate: String? = null,
) {

    fun toPantryItemCreateDTO() : PantryItemCreateDTO {
        return PantryItemCreateDTO(
            productEanCode=productEanCode,
            productName=productName ?: "",
            productDescription=description ?: "",
            productImageUrl=imageUrl,
            productAmount=amount?.toDouble() ?: 1.0,
            productUnit=unit ?: "",
            foodName=foodName ?: "",
            categoryName=category ?: "",
            brandName=brandName ?: "",
            validityDate=validityDate?.toDate() ?: Date()
        )
    }

}
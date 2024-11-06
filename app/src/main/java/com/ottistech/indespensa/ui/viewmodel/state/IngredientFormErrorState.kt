package com.ottistech.indespensa.ui.viewmodel.state

import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO

data class IngredientFormErrorState (
    var selectedProduct: String? = null,
    var amount: String? = null,
    var unit: String? = null,
)

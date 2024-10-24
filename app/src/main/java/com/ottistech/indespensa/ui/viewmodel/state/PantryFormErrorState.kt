package com.ottistech.indespensa.ui.viewmodel.state

data class PantryFormErrorState (
    var productName: String? = null,
    var foodName: String? = null,
    var category: String? = null,
    var description: String? = null,
    var brandName: String? = null,
    var amount: String? = null,
    var unit: String? = null,
    var validityDate: String? = null,
)
package com.ottistech.indespensa.ui.viewmodel.state

data class PantryFormActiveState (
    var imageUrl: Boolean = true,
    var productName: Boolean = true,
    var foodName: Boolean = true,
    var category: Boolean = true,
    var description: Boolean = true,
    var brandName: Boolean = true,
    var amount: Boolean = true,
    var unit: Boolean = true,
)

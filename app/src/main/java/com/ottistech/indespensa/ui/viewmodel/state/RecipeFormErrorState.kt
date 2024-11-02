package com.ottistech.indespensa.ui.viewmodel.state

data class RecipeFormErrorState (
    var name: String? = null,
    var description: String? = null,
    var time: String? = null,
    var level: String? = null,
    var ingredients: String? = null,
    var preparationMethod: String? = null
)
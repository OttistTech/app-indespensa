package com.ottistech.indespensa.ui.viewmodel.state

import com.ottistech.indespensa.shared.IngredientState
import com.ottistech.indespensa.shared.RecipeLevel

data class RecipeSearchFiltersState (
    var queryText: String? = QUERY_TEXT_DEFAULT_STATE,
    var level: RecipeLevel? = LEVEL_DEFAULT_VALUE,
    var availability: IngredientState? = AVAILABILITY_DEFAULT_VALUE,
    var preparationTimeRange: Pair<Int, Int> = PREPARATION_TIME_RANGE_DEFAULT_VALUE
) {

    companion object {
        val QUERY_TEXT_DEFAULT_STATE: String? = null
        val LEVEL_DEFAULT_VALUE: RecipeLevel? = null
        val AVAILABILITY_DEFAULT_VALUE: IngredientState? = null
        val PREPARATION_TIME_RANGE_DEFAULT_VALUE: Pair<Int, Int> = Pair(0, 1440)
    }

    fun resetState() {
        queryText = QUERY_TEXT_DEFAULT_STATE
        level = LEVEL_DEFAULT_VALUE
        availability = AVAILABILITY_DEFAULT_VALUE
        preparationTimeRange = PREPARATION_TIME_RANGE_DEFAULT_VALUE
    }
}
package com.ottistech.indespensa.webclient.dto.dashboard

import java.util.Date

data class PersonalDashboardDTO (
    val itemsInPantryCount: Int,
    val lastPurchaseDate: Date?,
    val itemsCloseValidityDateCount: Int,
    val possibleRecipesInPantryCount: Int
)

package com.ottistech.indespensa.webclient.dto.pantry

import java.util.Date

data class PantryItemAddDTO (
    val  shopItemId: Long,
    val validityDate : Date
)


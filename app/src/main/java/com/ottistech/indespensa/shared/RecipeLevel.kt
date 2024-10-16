package com.ottistech.indespensa.shared

enum class RecipeLevel(val level: String) {

    EASY("Fácil"),
    INTER("Inter"),
    ADVANCED("Avançado");

    val displayName: String
        get() = level

    companion object {
        fun fromString(level: String?): RecipeLevel? {
            return entries.find { it.level == level }
        }
    }
}
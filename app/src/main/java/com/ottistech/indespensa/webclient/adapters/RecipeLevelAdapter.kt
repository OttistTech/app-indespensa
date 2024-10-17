package com.ottistech.indespensa.webclient.adapters

import com.google.gson.*
import com.ottistech.indespensa.shared.RecipeLevel
import java.lang.reflect.Type

class RecipeLevelAdapter : JsonDeserializer<RecipeLevel> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RecipeLevel {
        val level = json?.asString
        return RecipeLevel.fromString(level) ?: RecipeLevel.EASY
    }
}
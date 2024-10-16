package com.ottistech.indespensa.ui.model

class Filter<T>(
    val type: FilterType,
    var value: T? = null,
    val text: String? = null,
    var isSelected: Boolean = false
) {
    fun copy(): Filter<T> {
        return Filter(type, value, text, isSelected)
    }
}